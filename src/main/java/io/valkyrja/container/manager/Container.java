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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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
        // Nothing is installed yet, so past the map there is nothing to read
        validateAliasMapIsNotCyclic(data.aliases(), type -> null);

        aliases.putAll(data.aliases());
        callbacks.putAll(data.callbacks());
        services.putAll(data.services());
        singletons.putAll(data.singletons());
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
        // Only the incoming aliases start a walk, and each walk reads the container past
        // the map it is given. Nothing is installed before the walks end.
        validateAliasMapIsNotCyclic(data.aliases(), this::getAliasedId);

        aliases.putAll(data.aliases());
        callbacks.putAll(data.callbacks());
        services.putAll(data.services());
        singletons.putAll(data.singletons());
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

            // A parent that binds an alias after a child is built checks only its own map.
            // The two can then hold a cycle this alias is no part of, so end the walk.
            if (!seen.add(aliasedId)) {
                return;
            }

            current = aliasedId;
        }
    }

    /**
     * Validate that no alias in a map points at a chain that returns to it.
     *
     * <p>Past the map, the walk reads {@code installed}. It is a parameter rather than a call to
     * {@link #getAliasedId}, because an overridable method reaches a subclass that a constructor
     * has not initialized.
     *
     * @param aliases the aliases that start a walk
     * @param installed the read for a type the map does not hold
     */
    private void validateAliasMapIsNotCyclic(
            Map<Class<?>, Class<?>> aliases, Function<Class<?>, @Nullable Class<?>> installed) {
        for (var alias : aliases.keySet()) {
            validateAliasChainIsNotCyclic(alias, aliases, installed);
        }
    }

    /**
     * Validate that the chain one alias starts does not return to it.
     *
     * @param alias the alias the walk starts from
     * @param aliases the aliases that start a walk
     * @param installed the read for a type the map does not hold
     */
    private void validateAliasChainIsNotCyclic(
            Class<?> alias,
            Map<Class<?>, Class<?>> aliases,
            Function<Class<?>, @Nullable Class<?>> installed) {
        Set<Class<?>> seen = new HashSet<>();
        seen.add(alias);
        Class<?> current = alias;
        Class<?> aliasedId;

        // Past the supplied aliases, the walk reads what the container answers already,
        // so it follows a chain the supplied map only reaches into.
        while ((aliasedId =
                        aliases.containsKey(current)
                                ? aliases.get(current)
                                : installed.apply(current))
                != null) {
            // The chain returns to the alias this walk started from, so the map the
            // caller supplied is what closes it. Name the edge that took it there.
            if (aliasedId.equals(alias)) {
                throw new ContainerCyclicAliasException(current.getName(), aliasedId.getName());
            }

            // A chain the container already held returns here. `bindAlias` ends its walk
            // for that state, so this entry point answers it the same way.
            if (!seen.add(aliasedId)) {
                return;
            }

            current = aliasedId;
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

        if (!isSingletonBinding(id)) {
            return null;
        }

        T instance = getServiceWithoutChecks(id, Map.of());
        if (instance == null) {
            return null;
        }

        // Two request threads can both reach a parent that has not built this id yet, so
        // the map decides which instance every reader gets.
        Object published = instances.putIfAbsent(id, instance);

        return published != null ? (T) published : instance;
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
