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
import org.jspecify.annotations.Nullable;

public class ChildContainer extends Container {

    private final ContainerContract parent;

    /**
     * The alias targets this container is resolving, per thread.
     *
     * <p>The read asks whether a chain came back to a target this resolution is already on, so the
     * state belongs to one call stack. A set shared between threads would read the first entry of a
     * second thread as that chain. A plain set would also race, so each thread holds its own.
     */
    private final ThreadLocal<Set<Class<?>>> targetsInFlight =
            ThreadLocal.withInitial(HashSet::new);

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
        if (super.isAlias(id)) {
            return super.getAliasedWithoutChecks(id, arguments);
        }

        Class<?> target = getParentAliasTarget(id);
        if (target == null) {
            return null;
        }

        // The child holds the same registration. One request must not hold one copy
        // for the alias and another for the target.
        if (resolvesInChild(target)) {
            return getTargetOnce(id, (Class<T>) target, arguments);
        }

        return parent.getAliased(id, arguments);
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
     * Walk the parent's chain of aliases, and return the last hop it reaches.
     *
     * @param id the alias type
     * @return the last hop, or null when the type is not an alias
     */
    private @Nullable Class<?> getParentAliasTarget(Class<?> id) {
        Class<?> current = id;
        Class<?> target = null;
        Class<?> aliasedId;
        Set<Class<?>> seen = new HashSet<>();
        seen.add(id);

        while ((aliasedId = parent.getAliasedId(current)) != null) {
            // A parent that is itself a child reads its own map and its parent's. A binding
            // made on either after it was built can close a chain between them.
            if (!seen.add(aliasedId)) {
                throw new ContainerCyclicAliasException(current.getName(), aliasedId.getName());
            }

            target = aliasedId;
            current = aliasedId;

            // The parent reads these before it follows an alias, so it can answer at this
            // hop rather than continue the chain.
            if ((parent.isDeferred(current) && !parent.isPublished(current))
                    || parent.isSingleton(current)
                    || parent.isService(current)) {
                break;
            }
        }

        return target;
    }

    /**
     * Check whether the child resolves the target of a parent-declared alias itself.
     *
     * @param target the target type
     * @return true if the child resolves it, rather than the parent
     */
    private boolean resolvesInChild(Class<?> target) {
        // The parent publishes before it reads any map, so this test comes first. The
        // parent's state and the child's callback each decide one half.
        if (parent.isDeferred(target) && !parent.isPublished(target) && isDeferred(target)) {
            return true;
        }

        if (parent.isSingletonInstance(target)) {
            return false;
        }

        // Both containers answer here, and each marker decides one half.
        return parent.isSingletonBinding(target) && isSingletonBinding(target);
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
        // A chain that closes across two walks returns here rather than to one walk. An
        // instance cached for the target has broken the chain, so read that first.
        Set<Class<?>> inFlight = targetsInFlight.get();

        if (!inFlight.add(target)) {
            // A target the child does not bind runs in the parent, which never returns
            // here, so only a factory this container ran can have registered one.
            Object registered = instances.get(target);

            if (registered != null) {
                return (T) registered;
            }

            throw new ContainerCyclicAliasException(id.getName(), target.getName());
        }

        try {
            return get(target, arguments);
        } finally {
            inFlight.remove(target);

            if (inFlight.isEmpty()) {
                // The outermost resolution has returned, so the thread holds no state for a
                // container a request discards.
                targetsInFlight.remove();
            }
        }
    }
}
