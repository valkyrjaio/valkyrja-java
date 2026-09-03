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
import java.util.function.BiFunction;
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
 *   <li>Parent's cached instance ({@code getSingletonInstance} via contract — safe reuse, frozen)
 *   <li>Child's copied singleton binding → create in child via base class logic
 * </ol>
 *
 * <p>A binding the parent holds runs here, with the child as the container, so the dependencies it
 * resolves come from the child. No lookup asks the parent to build, publish, or cache anything, so
 * no request can change the frozen parent.
 *
 * <p>An alias that only the parent declares points at the parent's binding, and this container
 * resolves that binding in its own scope. A chain of parent aliases that returns to a type it
 * already reached throws {@link ContainerCyclicAliasException}.
 *
 * @see NativeChildContainer for a direct field-access alternative requiring a concrete parent type
 */
public class ChildContainer extends Container {

    private final ContainerContract parent;

    public ChildContainer(ContainerContract parent, ContainerData parentData) {
        this.parent = parent;
        // Copy only the two maps the child needs for self-sufficient singleton resolution.
        // All other resolution reads the parent through the contract, and runs in the child.
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
        // The parent holds a resolved instance and the child does not, so the child reuses
        // the parent's copy. The read builds nothing and publishes nothing.
        if (!super.isSingletonInstance(id) && parent.isSingletonInstance(id)) {
            return parent.getSingletonInstance(id);
        }

        return super.getSingletonWithoutChecks(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        // The parent declares the binding and the child does not, so the child runs the
        // parent's factory with itself as the container. The factory then resolves its own
        // dependencies in the request scope.
        if (super.getServiceCallable(id) == null) {
            BiFunction<ContainerContract, Map<String, Object>, Object> callable =
                    parent.getServiceCallable(id);

            if (callable != null) {
                return (T) callable.apply(this, arguments);
            }
        }

        return super.getServiceWithoutChecks(id, arguments);
    }

    @Override
    protected @Nullable <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        if (super.isAlias(id)) {
            return super.getAliasedWithoutChecks(id, arguments);
        }

        Class<?> aliasedId = getParentAliasTarget(id);

        if (aliasedId == null) {
            return null;
        }

        return getParentAliasedTarget(aliasedId, arguments);
    }

    /**
     * Walk the parent's alias chain to the first type that resolves.
     *
     * @param id the alias type
     * @return the type to resolve, or null when the chain reaches none
     */
    private @Nullable Class<?> getParentAliasTarget(Class<?> id) {
        Set<Class<?>> seen = new HashSet<>();
        Class<?> current = id;
        Class<?> aliasedId;

        while ((aliasedId = parent.getAliasedId(current)) != null) {
            if (!seen.add(aliasedId)) {
                throw new ContainerCyclicAliasException(
                        id.getName(), current.getName(), aliasedId.getName());
            }

            current = aliasedId;

            if (isResolvable(current)) {
                return current;
            }
        }

        return null;
    }

    /**
     * Resolve the target of an alias that only the parent declares.
     *
     * @param <T> the service type
     * @param id the target type
     * @param arguments arguments passed to the service factory
     * @return the resolved instance
     */
    @SuppressWarnings("unchecked")
    private <T> T getParentAliasedTarget(Class<?> id, Map<String, Object> arguments) {
        // The alias belongs to the parent, so it points at the parent's copy first, and at
        // the copy the child built for an earlier lookup second.
        Object instance = parent.getSingletonInstance(id);

        if (instance == null) {
            instance = super.getSingletonInstance(id);
        }

        if (instance != null) {
            return (T) instance;
        }

        BiFunction<ContainerContract, Map<String, Object>, Object> callable =
                parent.getServiceCallable(id);

        // The parent declares no binding, so the child answers from its own maps.
        if (callable == null) {
            return (T) get(id, arguments);
        }

        // The parent's binding runs with the child as the container.
        Object built = callable.apply(this, arguments);

        // A singleton caches in the child, so one request holds one instance.
        if (isSingletonBinding(id)) {
            instances.put(id, built);
        }

        return (T) built;
    }

    /**
     * Check whether a type resolves without a further alias hop.
     *
     * @param id the service type
     * @return true when the child or the parent can answer it
     */
    private boolean isResolvable(Class<?> id) {
        return isSingleton(id) || isService(id) || super.isDeferred(id);
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

    @Override
    public @Nullable <T> T getSingletonInstance(Class<T> id) {
        T instance = super.getSingletonInstance(id);

        return instance != null ? instance : parent.getSingletonInstance(id);
    }

    @Override
    public @Nullable BiFunction<ContainerContract, Map<String, Object>, Object> getServiceCallable(
            Class<?> id) {
        BiFunction<ContainerContract, Map<String, Object>, Object> callable =
                super.getServiceCallable(id);

        return callable != null ? callable : parent.getServiceCallable(id);
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
