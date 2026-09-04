/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * A per-request child container that interacts with the parent exclusively through {@link
 * ContainerContract} — no direct field access required.
 *
 * <p>Works across all languages regardless of whether they support class inheritance with protected
 * field access (Java, PHP, Python, C++, C#) or not (Go, Rust, C). This is the portable, universally
 * compatible implementation.
 *
 * <p>The constructor copies two maps from the parent's {@link ContainerData}:
 *
 * <ul>
 *   <li>{@code singletons} — singleton registrations. Used to create singleton instances in the
 *       child's own context when the parent has a binding but no cached instance yet.
 *   <li>{@code callbacks} — lazy provider callbacks. Enables the child to publish deferred
 *       providers into its own context on first access, independently of the parent.
 * </ul>
 *
 * <p>The same {@link ContainerData} reference can be stored once after bootstrap and reused across
 * all requests. Each child copies from it at construction, so the source is never mutated.
 *
 * <p>Singleton resolution order (in {@link #getSingletonWithoutChecks}):
 *
 * <ol>
 *   <li>Child's own cached instance
 *   <li>Parent's cached instance ({@code isSingletonInstance} via contract — safe reuse, frozen)
 *   <li>Child's copied singleton binding → create in child via base class logic
 * </ol>
 *
 * @see NativeChildContainer for a direct field-access alternative requiring a concrete parent type
 */
public class ChildContainer extends Container {

    private final ContainerContract parent;

    public ChildContainer(ContainerContract parent, ContainerData parentData) {
        this.parent = parent;
        // Copy only the two maps the child needs for self-sufficient singleton resolution.
        // All other resolution delegates to the parent via contract.
        // parentData is immutable (record with Map.copyOf) — safe to reuse across requests.
        this.singletons.putAll(parentData.singletons());
        this.callbacks.putAll(parentData.callbacks());
        // instances stays empty — child builds its own per request
    }

    /**
     * Intercepts only the case where the parent has a cached instance but the child does not. All
     * other paths (child's own instances, creating from child's copied binding) are handled by the
     * base {@link Container#getSingletonWithoutChecks} using the child's own maps.
     */
    @Override
    protected @Nullable <T> T getSingletonWithoutChecks(Class<T> id) {
        // Parent has a resolved instance and child does not — reuse it (frozen, safe)
        if (!super.isSingletonInstance(id) && parent.isSingletonInstance(id)) {
            return parent.getSingleton(id);
        }

        // Child's own instances (step 1) and child's copied binding → create in child (step 3)
        return super.getSingletonWithoutChecks(id);
    }

    @Override
    protected @Nullable <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        if (!super.isService(id) && parent.isService(id)) {
            return parent.getService(id, arguments);
        }
        return super.getServiceWithoutChecks(id, arguments);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        Class<?> ownAlias = aliases.get(id);
        if (ownAlias != null) {
            return get((Class<T>) ownAlias, arguments);
        }

        Class<?> aliasedId = getParentAliasTarget(id);
        if (aliasedId == null) {
            return null;
        }

        // The parent holds the target as a singleton it has not built. Resolving it there
        // would build a second copy for a request that already holds the binding, so the
        // child builds its own.
        if (parent.isSingletonBinding(aliasedId) && !parent.isSingletonInstance(aliasedId)) {
            return get((Class<T>) aliasedId, arguments);
        }

        return parent.getAliased(id, arguments);
    }

    /**
     * Publish a deferred service using the child's copied callbacks. The callback's presence is
     * sufficient guard. Runs with the child as the container so bindings register into the child's
     * own maps.
     */
    @Override
    public void publish(Class<?> id) {
        Consumer<ContainerContract> callback = callbacks.get(id);
        if (callback == null) {
            return;
        }
        callback.accept(this);
        published.put(id, true);
    }

    @Override
    public @Nullable Class<?> getAliasedId(Class<?> alias) {
        Class<?> aliased = super.getAliasedId(alias);

        return aliased != null ? aliased : parent.getAliasedId(alias);
    }

    @Override
    public boolean isAlias(Class<?> id) {
        return super.isAlias(id) || parent.isAlias(id);
    }

    @Override
    public boolean isService(Class<?> id) {
        return super.isService(id) || parent.isService(id);
    }

    @Override
    public boolean isSingletonInstance(Class<?> id) {
        return super.isSingletonInstance(id) || parent.isSingletonInstance(id);
    }

    // isSingletonBinding is NOT overridden — child's copied singletons map is checked by
    // Container.isSingletonBinding (super) via this.singletons, which is sufficient.

    /**
     * Parent check must come first. If the parent already published a provider at bootstrap, the
     * child must not republish it — doing so would re-run the callback and re-register bindings.
     * The child's own published map (super.isPublished) tracks only what the child itself has
     * lazily published via its copied callbacks.
     */
    @Override
    public boolean isPublished(Class<?> id) {
        return super.isPublished(id) || parent.isPublished(id);
    }

    /**
     * Walk the parent's chain of aliases to the type it ends at.
     *
     * @param id the alias type
     * @return the type the chain ends at, or null when the type is not an alias
     */
    private @Nullable Class<?> getParentAliasTarget(Class<?> id) {
        Set<Class<?>> seen = new HashSet<>();
        Class<?> current = id;
        Class<?> target = null;
        Class<?> aliasedId;

        while ((aliasedId = parent.getAliasedId(current)) != null) {
            // bindAlias rejects a cycle, so one here arrived through setFromData.
            // Delegating to the parent would follow it until the stack ends.
            if (!seen.add(aliasedId)) {
                throw new ContainerCyclicAliasException(current.getName(), aliasedId.getName());
            }

            target = aliasedId;
            current = aliasedId;
        }

        return target;
    }
}
