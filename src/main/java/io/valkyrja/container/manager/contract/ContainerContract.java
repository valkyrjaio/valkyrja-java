/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager.contract;

import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.enum_.InvalidReferenceMode;
import java.util.Map;
import java.util.function.BiFunction;

/** Contract for the dependency injection container. */
public interface ContainerContract extends ProvidersAwareContract {

    /**
     * Get a data snapshot of the container's current bindings.
     *
     * @return container data
     */
    ContainerDataContract getData();

    /**
     * Merge bindings from a data object into this container.
     *
     * @param data the data to merge
     */
    void setFromData(ContainerDataContract data);

    /**
     * Check whether a given service type is registered.
     *
     * @param id the service type
     * @return true if the service is known to the container
     */
    boolean has(Class<?> id);

    /**
     * Bind a factory callable to a service type.
     *
     * @param <T> the service type
     * @param id the service type to bind
     * @param callable the factory function receiving the container and arguments
     * @return this container for chaining
     */
    <T> ContainerContract bind(
            Class<T> id, BiFunction<ContainerContract, Map<String, Object>, T> callable);

    /**
     * Bind an alias type to another registered service type.
     *
     * @param <T> the type
     * @param alias the alias type
     * @param id the target service type
     * @return this container for chaining
     */
    <T> ContainerContract bindAlias(Class<T> alias, Class<T> id);

    /**
     * Bind a singleton factory callable to a service type.
     *
     * <p>On first resolution the service is created and cached; subsequent calls return the same
     * instance.
     *
     * @param <T> the service type
     * @param id the service type to bind
     * @param callable the factory function receiving the container and arguments
     * @return this container for chaining
     */
    <T> ContainerContract bindSingleton(
            Class<T> id, BiFunction<ContainerContract, Map<String, Object>, T> callable);

    /**
     * Register a pre-built singleton instance for a service type.
     *
     * @param <T> the service type
     * @param id the service type
     * @param singleton the singleton instance
     * @return this container for chaining
     */
    <T> ContainerContract setSingleton(Class<T> id, T singleton);

    /**
     * Check whether a service type is registered as an alias.
     *
     * @param id the service type
     * @return true if it is an alias
     */
    boolean isAlias(Class<?> id);

    /**
     * Check whether a service type has a bound factory callable.
     *
     * @param id the service type
     * @return true if it is a service
     */
    boolean isService(Class<?> id);

    /**
     * Check whether a service type is registered as a singleton (binding or cached instance).
     *
     * @param id the service type
     * @return true if it is a singleton
     */
    boolean isSingleton(Class<?> id);

    /**
     * Check whether a singleton instance is already resolved and cached in the instances map.
     *
     * @param id the service type
     * @return true if a cached instance exists
     */
    boolean isSingletonInstance(Class<?> id);

    /**
     * Check whether a singleton class binding is registered but not necessarily resolved yet.
     *
     * @param id the service type
     * @return true if a singleton binding exists in the singletons map
     */
    boolean isSingletonBinding(Class<?> id);

    /**
     * Resolve a service from the container using the default mode.
     *
     * @param <T> the service type
     * @param id the service type
     * @return the resolved instance
     */
    <T> T get(Class<T> id);

    /**
     * Resolve a service from the container with arguments.
     *
     * @param <T> the service type
     * @param id the service type
     * @param arguments arguments passed to the factory/constructor
     * @return the resolved instance
     */
    <T> T get(Class<T> id, Map<String, Object> arguments);

    /**
     * Resolve a service from the container with arguments and a fallback mode.
     *
     * @param <T> the service type
     * @param id the service type
     * @param arguments arguments passed to the factory/constructor
     * @param mode behaviour when the service is not found
     * @return the resolved instance
     */
    <T> T get(Class<T> id, Map<String, Object> arguments, InvalidReferenceMode mode);

    /**
     * Resolve an aliased service type.
     *
     * @param <T> the service type
     * @param id the alias type
     * @param arguments arguments passed to the factory/constructor
     * @return the resolved instance
     */
    <T> T getAliased(Class<T> id, Map<String, Object> arguments);

    /**
     * Resolve a service-bound service type.
     *
     * @param <T> the service type
     * @param id the service type
     * @param arguments arguments passed to the service factory
     * @return the resolved instance
     */
    <T> T getService(Class<T> id, Map<String, Object> arguments);

    /**
     * Resolve a singleton-bound service type.
     *
     * @param <T> the service type
     * @param id the service type
     * @return the singleton instance
     */
    <T> T getSingleton(Class<T> id);
}
