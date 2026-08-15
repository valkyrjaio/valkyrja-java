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
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import io.valkyrja.container.throwable.exception.ContainerUnpublishedParentTargetException;
import io.valkyrja.container.throwable.exception.ContainerUnresolvedParentAliasException;
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
            if (isUnpublishedInParent(id)) {
                // Delegating would run the parent's publish callback, so answer from
                // the child instead.
                T instance = super.getSingletonWithoutChecks(id);

                if (instance != null) {
                    return instance;
                }

                // get() tries the child's service and alias maps after this, and
                // getSingleton() does not, so refuse only when neither can answer.
                if (super.isService(id) || super.isAlias(id)) {
                    return null;
                }

                throw new ContainerUnpublishedParentTargetException(id.getName());
            }

            return parent.getSingleton(id);
        }

        // Child's own instances (step 1) and child's copied binding → create in child (step 3)
        return super.getSingletonWithoutChecks(id);
    }

    @Override
    protected @Nullable <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        if (!super.isService(id) && parent.isService(id)) {
            if (isUnpublishedInParent(id)) {
                // get() tries the child's alias map after this, and getService() does
                // not, so refuse only when that cannot answer either.
                if (super.isAlias(id)) {
                    return null;
                }

                throw new ContainerUnpublishedParentTargetException(id.getName());
            }

            return parent.getService(id, arguments);
        }
        return super.getServiceWithoutChecks(id, arguments);
    }

    @Override
    protected @Nullable <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        if (super.isAlias(id)) {
            return super.getAliasedWithoutChecks(id, arguments);
        }

        if (!parent.isAlias(id)) {
            return null;
        }

        validateParentAliasResolution(id);

        return parent.getAliased(id, arguments);
    }

    /**
     * Validate that the parent answers an alias without caching anything new.
     *
     * @param id the alias type
     */
    protected void validateParentAliasResolution(Class<?> id) {
        Set<Class<?>> seen = new HashSet<>();
        Class<?> current = id;
        Class<?> aliasedId;

        while ((aliasedId = parent.getAliasedId(current)) != null) {
            if (!seen.add(aliasedId)) {
                throw new ContainerInvalidReferenceException(id.getName());
            }

            current = aliasedId;

            if (isUnresolvedInParent(current)) {
                throw new ContainerUnresolvedParentAliasException(id.getName(), current.getName());
            }

            // The parent answers a singleton or a service before it follows an alias,
            // so it never reaches the rest of the chain.
            if (parent.isSingletonInstance(current) || parent.isService(current)) {
                return;
            }
        }
    }

    /**
     * Check whether the parent would cache a given type for the first time.
     *
     * @param id the service type
     * @return true if the parent would write while answering it
     */
    protected boolean isUnresolvedInParent(Class<?> id) {
        // The parent publishes before it reads any map, so this test comes first.
        if (isUnpublishedInParent(id)) {
            return true;
        }

        if (parent.isSingletonInstance(id)) {
            return false;
        }

        return parent.isSingletonBinding(id);
    }

    /**
     * Check whether the parent holds a publish callback it has not run.
     *
     * @param id the service type
     * @return true if the callback is registered and unrun
     */
    protected boolean isUnpublishedInParent(Class<?> id) {
        return parent.isDeferred(id) && !parent.isPublished(id);
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
    public boolean isAlias(Class<?> id) {
        return super.isAlias(id) || parent.isAlias(id);
    }

    @Override
    public @Nullable Class<?> getAliasedId(Class<?> alias) {
        Class<?> aliased = super.getAliasedId(alias);

        return aliased != null ? aliased : parent.getAliasedId(alias);
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
}
