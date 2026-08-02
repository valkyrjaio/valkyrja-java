/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager.contract;

import io.valkyrja.container.provider.contract.ServiceProviderContract;

/** Contract for objects that are aware of and manage service providers. */
public interface ProvidersAwareContract {

    /**
     * Register a provider.
     *
     * @param provider the provider instance to register
     */
    void register(ServiceProviderContract provider);

    /**
     * Determine whether a given service has been published.
     *
     * @param id the service type
     * @return true if the service is published
     */
    boolean isPublished(Class<?> id);

    /**
     * Publish a deferred service by invoking its provider's publish callback.
     *
     * @param id the service type to publish
     */
    void publish(Class<?> id);
}
