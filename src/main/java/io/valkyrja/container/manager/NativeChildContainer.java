/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
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
 * <p>All writes go to the child's own maps only. The parent is never mutated after bootstrap.
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
        if (isUnbuiltInParent(target)) {
            return get((Class<T>) target, arguments);
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

            // The parent answers a singleton or a service before it follows an alias,
            // so it never reaches the rest of the chain.
            if (parent.singletons.containsKey(current)
                    || parent.instances.containsKey(current)
                    || parent.services.containsKey(current)) {
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

        if (parent.instances.containsKey(id)) {
            return false;
        }

        return parent.singletons.containsKey(id);
    }
}
