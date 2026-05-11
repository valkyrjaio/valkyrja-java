/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.enum_.InvalidReferenceMode;
import io.valkyrja.container.manager.abstract_.ProvidersAware;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Default dependency injection container implementation.
 *
 * <p>Resolution priority:
 *
 * <ol>
 *   <li>Cached singleton instance
 *   <li>Service callable factory (covers both regular and singleton bindings)
 *   <li>Alias (redirects to another service type)
 *   <li>Fallback (new instance or exception depending on {@link InvalidReferenceMode})
 * </ol>
 */
public class Container extends ProvidersAware {

    /** alias type → target type */
    protected final Map<Class<?>, Class<?>> aliases = new HashMap<>();

    /** service type → cached singleton instance */
    protected final Map<Class<?>, Object> instances = new HashMap<>();

    /** service type → factory callable */
    protected final Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>>
            services = new HashMap<>();

    /** service type → itself (self-map, tracks which service types are singletons) */
    protected final Map<Class<?>, Class<?>> singletons = new HashMap<>();

    public Container() {
        this(new ContainerData());
    }

    public Container(ContainerDataContract data) {
        aliases.putAll(data.aliases());
        deferred.putAll(data.deferred());
        deferredCallbacks.putAll(data.deferredCallbacks());
        services.putAll(data.services());
        singletons.putAll(data.singletons());
    }

    @Override
    public ContainerDataContract getData() {
        return new ContainerData(
                Map.copyOf(aliases),
                Map.copyOf(deferred),
                Map.copyOf(deferredCallbacks),
                Map.copyOf(services),
                Map.copyOf(singletons),
                List.copyOf(providers));
    }

    @Override
    public void setFromData(ContainerDataContract data) {
        aliases.putAll(data.aliases());
        deferred.putAll(data.deferred());
        deferredCallbacks.putAll(data.deferredCallbacks());
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
        aliases.put(alias, id);
        return this;
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
        return get(id, Map.of(), InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION);
    }

    @Override
    public <T> T get(Class<T> id, Map<String, Object> arguments) {
        return get(id, arguments, InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION);
    }

    @Override
    public <T> T get(Class<T> id, Map<String, Object> arguments, InvalidReferenceMode mode) {
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

        return getFallback(id, arguments, mode);
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
    protected <T> T getAliasedWithoutChecks(Class<T> id, Map<String, Object> arguments) {
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
    protected <T> T getSingletonWithoutChecks(Class<T> id) {
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
    protected <T> T getServiceWithoutChecks(Class<T> id, Map<String, Object> arguments) {
        BiFunction<ContainerContract, Map<String, Object>, Object> callable = services.get(id);
        if (callable == null) {
            return null;
        }
        return (T) callable.apply(this, arguments);
    }

    /** Fallback behavior when no binding is found for the given service type. */
    protected <T> T getFallback(
            Class<T> id, Map<String, Object> arguments, InvalidReferenceMode mode) {
        return switch (mode) {
            case THROW_EXCEPTION -> throw new ContainerInvalidReferenceException(id.getName());
            case NEW_INSTANCE_OR_THROW_EXCEPTION -> newInstanceOrThrow(id, arguments);
        };
    }

    /**
     * Attempt to instantiate the class directly via its no-arg constructor, throwing {@link
     * ContainerInvalidReferenceException} if not possible.
     */
    protected <T> T newInstanceOrThrow(Class<T> id, Map<String, Object> arguments) {
        try {
            return id.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ContainerInvalidReferenceException(id.getName(), e);
        }
    }

    /**
     * Package-private accessor for ChildContainer — exposes the deferred callback for a given id
     * without putting it on the public contract.
     */
    Consumer<ContainerContract> getDeferredCallback(Class<?> id) {
        return deferredCallbacks.get(id);
    }

    /** Publish a deferred service if it has not been published yet. */
    protected void publishUnpublishedDeferred(Class<?> id) {
        if (isDeferred(id) && !isPublished(id)) {
            publish(id);
        }
    }
}
