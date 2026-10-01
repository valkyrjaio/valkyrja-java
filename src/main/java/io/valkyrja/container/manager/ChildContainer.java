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

    public ChildContainer(ContainerContract parent, ContainerData parentData) {
        this.parent = parent;
        // Copy only the two maps the child needs for self-sufficient singleton resolution.
        // All other resolution delegates to the parent via contract.
        // parentData is immutable (record with Map.copyOf) — safe to reuse across requests.
        this.singletons.putAll(parentData.singletons());
        this.callbacks.putAll(parentData.callbacks());
        // instances stays empty — child builds its own per request
    }

    /** The alias targets this container is resolving. */
    private final Set<Class<?>> targetsInFlight = new HashSet<>();

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

        // The parent would resolve this target for the first time, and the child holds
        // the same registration, so letting the parent do it would leave the request
        // with one copy for the alias and another for the id.
        if (isUnbuiltInParent(target)) {
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
     * Walk the parent's chain of aliases to the type the parent would answer.
     *
     * @param id the alias type
     * @return the type the parent answers, or null when the type is not an alias
     */
    private @Nullable Class<?> getParentAliasTarget(Class<?> id) {
        Class<?> current = id;
        Class<?> target = null;
        Class<?> aliasedId;
        Set<Class<?>> seen = new HashSet<>();
        seen.add(id);

        while ((aliasedId = parent.getAliasedId(current)) != null) {
            // A parent that is itself a child reads its own map and its parent's, and a
            // binding made on either after it was built can close a chain between them.
            if (!seen.add(aliasedId)) {
                throw new ContainerCyclicAliasException(current.getName(), aliasedId.getName());
            }

            target = aliasedId;
            current = aliasedId;

            // The parent publishes, then reads its maps, and only then follows an
            // alias, so it never reaches the rest of the chain from any of these.
            if ((parent.isDeferred(current) && !parent.isPublished(current))
                    || parent.isSingleton(current)
                    || parent.isService(current)) {
                break;
            }
        }

        return target;
    }

    /**
     * Check whether the parent would resolve a type for the first time.
     *
     * @param id the target type
     * @return true if the parent would write while answering it
     */
    private boolean isUnbuiltInParent(Class<?> id) {
        // The parent publishes before it reads any map, so this test comes first.
        if (parent.isDeferred(id) && !parent.isPublished(id)) {
            return true;
        }

        if (parent.isSingletonInstance(id)) {
            return false;
        }

        // The child reads its own marker here, because the child is the container that
        // caches what it builds. A marker it does not hold leaves the lookup to the parent.
        return isSingletonBinding(id);
    }

    /**
     * Resolve an alias target, and reject a chain that returns to one already in flight.
     *
     * @param id the alias
     * @param target the target type
     * @param arguments the arguments
     * @return the instance the target resolves to
     */
    private <T> T getTargetOnce(Class<?> id, Class<T> target, Map<String, Object> arguments) {
        // A walk ends at the first hop the parent would answer, so a chain that closes
        // across two of them returns here rather than to one walk. Name the pair.
        if (!targetsInFlight.add(target)) {
            throw new ContainerCyclicAliasException(id.getName(), target.getName());
        }

        try {
            return get(target, arguments);
        } finally {
            targetsInFlight.remove(target);
        }
    }
}
