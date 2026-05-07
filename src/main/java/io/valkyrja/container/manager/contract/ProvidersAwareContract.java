/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager.contract;

import io.valkyrja.container.provider.contract.ServiceProviderContract;

/** Contract for objects that are aware of and manage service providers. */
public interface ProvidersAwareContract {

    /**
     * Register a provider.
     *
     * @param providerClass the provider class to register
     */
    void register(Class<? extends ServiceProviderContract> providerClass);

    /**
     * Determine whether a given service is deferred and not yet published.
     *
     * @param id the service type
     * @return true if the service is deferred
     */
    boolean isDeferred(Class<?> id);

    /**
     * Determine whether a given service has been published.
     *
     * @param id the service type
     * @return true if the service is published
     */
    boolean isPublished(Class<?> id);

    /**
     * Determine whether a provider has been registered.
     *
     * @param providerClass the provider class
     * @return true if already registered
     */
    boolean isRegistered(Class<? extends ServiceProviderContract> providerClass);

    /**
     * Publish a deferred service by invoking its provider's publish callback.
     *
     * @param id the service type to publish
     */
    void publish(Class<?> id);
}
