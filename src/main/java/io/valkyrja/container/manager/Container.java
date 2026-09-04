/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.abstract_.ProvidersAware;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * Default dependency injection container implementation.
 *
 * <p>Resolution priority:
 *
 * <ol>
 *   <li>Cached singleton instance
 *   <li>Service callable factory (covers both regular and singleton bindings)
 *   <li>Alias (redirects to another service type)
 * </ol>
 *
 * <p>A service type that none of the three resolves raises {@link
 * ContainerInvalidReferenceException}. The container builds nothing that a binding does not
 * describe.
 */
public class Container extends ProvidersAware {

    /** alias type → target type */
    protected final Map<Class<?>, Class<?>> aliases = new ConcurrentHashMap<>();

    /** service type → cached singleton instance */
    protected final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    /** service type → factory callable */
    protected final Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>>
            services = new ConcurrentHashMap<>();

    /** service type → itself (self-map, tracks which service types are singletons) */
    protected final Map<Class<?>, Class<?>> singletons = new ConcurrentHashMap<>();

    public Container() {
        this(new ContainerData());
    }

    public Container(ContainerDataContract data) {
        aliases.putAll(data.aliases());
        callbacks.putAll(data.callbacks());
        services.putAll(data.services());
        singletons.putAll(data.singletons());

        validateAliasesAreNotCyclic();
    }

    @Override
    public ContainerDataContract getData() {
        return new ContainerData(
                Map.copyOf(aliases),
                Map.copyOf(callbacks),
                Map.copyOf(services),
                Map.copyOf(singletons));
    }

    @Override
    public void setFromData(ContainerDataContract data) {
        Map<Class<?>, Class<?>> originalAliases = Map.copyOf(aliases);

        aliases.putAll(data.aliases());
        callbacks.putAll(data.callbacks());
        services.putAll(data.services());
        singletons.putAll(data.singletons());

        try {
            validateAliasesAreNotCyclic();
        } catch (ContainerCyclicAliasException exception) {
            // A caller that catches this keeps the container it had, not a cyclic map
            aliases.clear();
            aliases.putAll(originalAliases);

            throw exception;
        }
    }

    @Override
    public boolean has(Class<?> id) {
        return isDeferred(id) || isSingleton(id) || isService(id) || isAlias(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ContainerContract bind(
            Class<T> id, BiFunction<ContainerContract, Map<String, Object>, T> callable) {
        services.put(id, (BiFunction<ContainerContract, Map<String, Object>, Object>) callable);
        published.put(id, true);
        return this;
    }

    @Override
    public <T> ContainerContract bindAlias(Class<T> alias, Class<T> id) {
        validateAliasIsNotCyclic(alias, id);

        aliases.put(alias, id);
        return this;
    }

    /**
     * Validate that an alias does not point at a chain that returns to it.
     *
     * @param alias the alias being bound
     * @param id the type the alias points at
     */
    protected void validateAliasIsNotCyclic(Class<?> alias, Class<?> id) {
        if (alias.equals(id)) {
            throw new ContainerCyclicAliasException(alias.getName(), id.getName());
        }

        Set<Class<?>> seen = new HashSet<>();
        Class<?> current = id;
        Class<?> aliasedId;

        while ((aliasedId = getAliasedId(current)) != null) {
            if (aliasedId.equals(alias)) {
                throw new ContainerCyclicAliasException(alias.getName(), id.getName());
            }

            // A cycle this alias is no part of would spin here. The sweep below reaches
            // every alias, so the walk that starts inside that cycle throws for it.
            if (!seen.add(aliasedId)) {
                return;
            }

            current = aliasedId;
        }
    }

    /**
     * Validate that no alias in the map points at a chain that returns to it.
     *
     * <p>Private, because a constructor calls it. An overridable method there reaches a subclass
     * before the subclass is initialized.
     */
    private void validateAliasesAreNotCyclic() {
        for (var alias : aliases.entrySet()) {
            validateAliasIsNotCyclic(alias.getKey(), alias.getValue());
        }
    }

    @Override
    public <T> ContainerContract bindSingleton(
            Class<T> id, BiFunction<ContainerContract, Map<String, Object>, T> callable) {
        singletons.put(id, id);
        bind(id, callable);
        return this;
    }

    @Override
    public <T> ContainerContract setSingleton(Class<T> id, T singleton) {
        instances.put(id, singleton);
        published.put(id, true);
        return this;
    }

    @Override
    public @Nullable Class<?> getAliasedId(Class<?> alias) {
        return aliases.get(alias);
    }

    @Override
    public boolean isAlias(Class<?> id) {
        return aliases.containsKey(id);
    }

    @Override
    public boolean isService(Class<?> id) {
        return services.containsKey(id);
    }

    @Override
    public boolean isSingleton(Class<?> id) {
        return isSingletonBinding(id) || isSingletonInstance(id);
    }

    @Override
    public boolean isSingletonInstance(Class<?> id) {
        return instances.containsKey(id);
    }

    @Override
    public boolean isSingletonBinding(Class<?> id) {
        return singletons.containsKey(id);
    }

    @Override
    public <T> T get(Class<T> id) {
        return get(id, Map.of());
    }

    @Override
    public <T> T get(Class<T> id, Map<String, Object> arguments) {
        publishUnpublishedDeferred(id);

        T singleton = getSingletonWithoutChecks(id);
        if (singleton != null) {
            return singleton;
        }

        T service = getServiceWithoutChecks(id, arguments);
        if (service != null) {
            return service;
        }

        T aliased = getAliasedWithoutChecks(id, arguments);
        if (aliased != null) {
            return aliased;
        }

        throw new ContainerInvalidReferenceException(id.getName());
    }

    @Override
    public <T> T getAliased(Class<T> id, Map<String, Object> arguments) {
        T aliased = getAliasedWithoutChecks(id, arguments);
        if (aliased == null) {
            throw new ContainerInvalidReferenceException(id.getName());
        }
        return aliased;
    }

    @Override
    public <T> T getService(Class<T> id, Map<String, Object> arguments) {
        publishUnpublishedDeferred(id);
        T service = getServiceWithoutChecks(id, arguments);
        if (service == null) {
            throw new ContainerInvalidReferenceException(id.getName());
        }
        return service;
    }

    @Override
    public <T> T getSingleton(Class<T> id) {
        publishUnpublishedDeferred(id);
        T singleton = getSingletonWithoutChecks(id);
        if (singleton == null) {
            throw new ContainerInvalidReferenceException(id.getName());
        }
        return singleton;
    }

    /** Resolve an aliased service without ensuring publication. */
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        Class<?> aliased = aliases.get(id);
        if (aliased == null) {
            return null;
        }
        return get((Class<T>) aliased, arguments);
    }

    /**
     * Resolve a singleton without ensuring publication.
     *
     * <p>Returns a cached instance if available, or creates and caches one if the service is
     * registered as a singleton.
     */
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getSingletonWithoutChecks(Class<T> id) {
        Object cached = instances.get(id);
        if (cached != null) {
            return (T) cached;
        }

        if (!singletons.containsKey(id)) {
            return null;
        }

        T instance = getServiceWithoutChecks(id, Map.of());
        if (instance != null) {
            instances.put(id, instance);
        }
        return instance;
    }

    /** Resolve a service via its registered callable without ensuring publication. */
    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        BiFunction<ContainerContract, Map<String, Object>, Object> callable = services.get(id);
        if (callable == null) {
            return null;
        }
        return (T) callable.apply(this, arguments);
    }

    /**
     * Package-private accessor for NativeChildContainer — exposes the deferred callback for a given
     * id without putting it on the public contract.
     */
    @Nullable Consumer<ContainerContract> getCallback(Class<?> id) {
        return callbacks.get(id);
    }

    /** Publish a deferred service if it has not been published yet. */
    protected void publishUnpublishedDeferred(Class<?> id) {
        if (isDeferred(id) && !isPublished(id)) {
            publish(id);
        }
    }
}
