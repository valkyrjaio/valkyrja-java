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

public class NativeChildContainer extends Container {

    private final Container parent;

    public NativeChildContainer(Container parent) {
        this.parent = parent;
    }

    /**
     * The alias targets this container is resolving.
     *
     * <p>One resolution reads it, to see a chain that came back to a target it is already
     * resolving. A set shared between threads would read the first entry of a second thread as that
     * chain, so this one is not concurrent, unlike the maps a parent shares.
     */
    private final Set<Class<?>> targetsInFlight = new HashSet<>();

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
        if (!isSingletonBinding(id)) {
            return null;
        }

        // Create in child, cache in child only — parent never touched. A factory can
        // register this id itself while it runs, so the map decides which instance every
        // reader gets.
        T instance = getServiceWithoutChecks(id, Map.of());
        if (instance == null) {
            return null;
        }

        Object published = instances.putIfAbsent(id, instance);

        return published != null ? (T) published : instance;
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
        if (aliases.containsKey(id)) {
            return super.getAliasedWithoutChecks(id, arguments);
        }

        Class<?> target = getParentAliasTarget(id);
        if (target == null) {
            return null;
        }

        // The parent would resolve this target for the first time, and the child holds
        // the same registration, so letting the parent do it would leave the request
        // with one copy for the alias and another for the id.
        if (resolvesInChild(target)) {
            return getTargetOnce(id, (Class<T>) target, arguments);
        }

        return parent.getAliased(id, arguments);
    }

    /**
     * Read a publish callback from the child, then the parent.
     *
     * <p>PHP reads the parent's callbacks through this one accessor, and the base publish follows
     * it. Java cannot: callbacks lives in a different sub-package, so a protected accessor is
     * unreachable on a sibling instance, and publish reads the map itself. The two overrides below
     * carry what the accessor carries in PHP.
     */
    @Override
    @Nullable Consumer<ContainerContract> getCallback(Class<?> id) {
        Consumer<ContainerContract> callback = callbacks.get(id);

        return callback != null ? callback : parent.getCallback(id);
    }

    @Override
    public boolean isDeferred(Class<?> id) {
        return getCallback(id) != null;
    }

    /** Run the callback with the child as the container, so its bindings land in the child. */
    @Override
    public void publish(Class<?> id) {
        Consumer<ContainerContract> callback = getCallback(id);

        if (callback == null) {
            return;
        }

        callback.accept(this);

        published.put(id, true);
    }

    @Override
    public @Nullable Class<?> getAliasedId(Class<?> alias) {
        Class<?> aliased = aliases.get(alias);

        return aliased != null ? aliased : parent.aliases.get(alias);
    }

    @Override
    public boolean isAlias(Class<?> id) {
        return aliases.containsKey(id) || parent.aliases.containsKey(id);
    }

    @Override
    public boolean isService(Class<?> id) {
        return services.containsKey(id) || parent.services.containsKey(id);
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

    /**
     * Walk the parent's chain of aliases to the type the parent would answer.
     *
     * @param id the alias type
     * @return the type the parent answers, or null when the type is not an alias
     */
    private @Nullable Class<?> getParentAliasTarget(Class<?> id) {
        Class<?> current = id;
        Class<?> target = null;
        Class<?> aliasedId;

        while ((aliasedId = parent.aliases.get(current)) != null) {
            target = aliasedId;
            current = aliasedId;

            // The parent publishes, then reads its maps, and only then follows an
            // alias, so it never reaches the rest of the chain from any of these.
            if ((parent.getCallback(current) != null && !parent.isPublished(current))
                    || parent.singletons.containsKey(current)
                    || parent.instances.containsKey(current)
                    || parent.services.containsKey(current)) {
                break;
            }
        }

        return target;
    }

    /**
     * Check whether the child resolves the target of a parent-declared alias itself.
     *
     * @param id the target type
     * @return true if the child resolves it, rather than the parent
     */
    private boolean resolvesInChild(Class<?> id) {
        // The parent publishes before it reads any map, so this test comes first. This
        // class copies no callback map, so the parent's callback is the child's as well.
        if (parent.isDeferred(id) && !parent.isPublished(id)) {
            return true;
        }

        if (parent.instances.containsKey(id)) {
            return false;
        }

        // This class copies no map, so the parent's marker is the child's as well. One read
        // carries what the portable child needs two for.
        return parent.singletons.containsKey(id);
    }

    /**
     * Resolve an alias target, and reject a chain that returns to one already in flight.
     *
     * @param id the alias
     * @param target the target type
     * @param arguments the arguments
     * @return the instance the target resolves to
     */
    @SuppressWarnings("unchecked")
    private <T> T getTargetOnce(Class<?> id, Class<T> target, Map<String, Object> arguments) {
        // A walk ends at the first hop the parent would answer, so a chain that closes
        // across two of them returns here rather than to one walk. A factory that
        // registered its own id while it runs has broken the chain, so read that first,
        // and name the pair only when nothing can answer.
        if (!targetsInFlight.add(target)) {
            // The factory receives the child, so the child's map is where a registration
            // made during this resolution lands.
            Object registered = instances.get(target);

            if (registered != null) {
                return (T) registered;
            }

            throw new ContainerCyclicAliasException(id.getName(), target.getName());
        }

        try {
            return get(target, arguments);
        } finally {
            targetsInFlight.remove(target);
        }
    }
}
