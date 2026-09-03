/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * A per-request child container that accesses parent state via direct protected field reads.
 *
 * <p>Requires the parent to be a concrete {@link Container} instance (same package). No map copies
 * at construction — parent fields are read directly, giving zero per-request allocation beyond the
 * child's own empty maps.
 *
 * <p>All writes go to the child's own maps only. The parent is never mutated after bootstrap, and
 * no lookup asks the parent to build, publish, or cache anything.
 *
 * <p>An alias that only the parent declares points at the parent's binding, and this container
 * resolves that binding in its own scope. A chain of parent aliases that returns to a type it
 * already reached throws {@link ContainerCyclicAliasException}.
 *
 * <p>Singleton resolution order:
 *
 * <ol>
 *   <li>Child's own cached instance
 *   <li>Parent's cached instance (direct field read — no method dispatch, no creation)
 *   <li>Child or parent singleton binding → create in child, cache in child only
 * </ol>
 *
 * @see ChildContainer for a contract-only alternative that works across all languages
 */
public class NativeChildContainer extends Container {

    private final Container parent;

    public NativeChildContainer(Container parent) {
        this.parent = parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getSingletonWithoutChecks(Class<T> id) {
        // 1. Child's own cached instance
        Object cached = instances.get(id);
        if (cached != null) {
            return (T) cached;
        }

        // 2. Parent's cached instance — direct field read, no creation, no method dispatch
        Object parentCached = parent.instances.get(id);
        if (parentCached != null) {
            return (T) parentCached;
        }

        // 3. No binding in child or parent → nothing to create
        if (!singletons.containsKey(id) && !parent.singletons.containsKey(id)) {
            return null;
        }

        // Create in child, cache in child only — parent never touched
        T instance = getServiceWithoutChecks(id, Map.of());
        if (instance != null) {
            instances.put(id, instance);
        }
        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        BiFunction<ContainerContract, Map<String, Object>, Object> callable = services.get(id);
        if (callable == null) {
            callable = parent.services.get(id);
        }
        if (callable == null) {
            return null;
        }
        return (T) callable.apply(this, arguments);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        Class<?> aliased = aliases.get(id);
        if (aliased != null) {
            return get((Class<T>) aliased, arguments);
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

        while ((aliasedId = parent.aliases.get(current)) != null) {
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
        Object instance = parent.instances.get(id);
        if (instance == null) {
            instance = instances.get(id);
        }
        if (instance != null) {
            return (T) instance;
        }

        BiFunction<ContainerContract, Map<String, Object>, Object> callable =
                parent.services.get(id);

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
        return isSingleton(id) || isService(id) || isDeferred(id);
    }

    /**
     * Publish a deferred service using child or parent callbacks. Consults parent via the
     * package-private {@link Container#getCallback} accessor when the child has no callback of its
     * own. Runs with the child as the container so bindings register into the child's own maps.
     */
    @Override
    public void publish(Class<?> id) {
        Consumer<ContainerContract> callback = callbacks.get(id);
        if (callback == null) {
            callback = parent.getCallback(id);
        }
        if (callback == null) {
            return;
        }
        callback.accept(this);
        published.put(id, true);
    }

    /** Publish a deferred provider registered in either the child or the parent on first access. */
    @Override
    protected void publishUnpublishedDeferred(Class<?> id) {
        if (isDeferred(id) && !isPublished(id)) {
            publish(id);
        }
    }

    @Override
    public boolean isDeferred(Class<?> id) {
        return super.isDeferred(id) || parent.getCallback(id) != null;
    }

    @Override
    public @Nullable Class<?> getAliasedId(Class<?> alias) {
        Class<?> aliased = aliases.get(alias);

        return aliased != null ? aliased : parent.aliases.get(alias);
    }

    @Override
    public boolean isAlias(Class<?> id) {
        return getAliasedId(id) != null;
    }

    @Override
    public boolean isService(Class<?> id) {
        return services.containsKey(id) || parent.services.containsKey(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getSingletonInstance(Class<T> id) {
        Object instance = instances.get(id);

        return (T) (instance != null ? instance : parent.instances.get(id));
    }

    @Override
    public @Nullable BiFunction<ContainerContract, Map<String, Object>, Object> getServiceCallable(
            Class<?> id) {
        BiFunction<ContainerContract, Map<String, Object>, Object> callable = services.get(id);

        return callable != null ? callable : parent.services.get(id);
    }

    @Override
    public boolean isSingletonInstance(Class<?> id) {
        // instances is in Container (same package) — direct field access works
        return instances.containsKey(id) || parent.instances.containsKey(id);
    }

    @Override
    public boolean isSingletonBinding(Class<?> id) {
        // singletons is in Container (same package) — direct field access works
        return singletons.containsKey(id) || parent.singletons.containsKey(id);
    }

    @Override
    public boolean isPublished(Class<?> id) {
        // published is in ProvidersAware (different sub-package) — use contract
        return super.isPublished(id) || parent.isPublished(id);
    }
}
