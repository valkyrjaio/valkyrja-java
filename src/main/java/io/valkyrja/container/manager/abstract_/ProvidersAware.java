/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager.abstract_;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.manager.contract.ProvidersAwareContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.container.throwable.exception.ContainerInvalidPublishCallbackException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ProvidersAware implements ProvidersAwareContract, ContainerContract {

    /** service type → publish callback */
    protected final Map<Class<?>, Consumer<ContainerContract>> callbacks = new HashMap<>();

    /** service type → published flag */
    protected final Map<Class<?>, Boolean> published = new HashMap<>();

    @Override
    public void register(ServiceProviderContract provider) {
        for (var publisher : provider.publishers().entrySet()) {
            Class<?> provided = publisher.getKey();
            Consumer<ContainerContract> callback = publisher.getValue();

            if (callback == null) {
                throw new ContainerInvalidPublishCallbackException(
                        provided.getName() + " should have a valid publisher callback");
            }

            callbacks.put(provided, callback);
        }
    }

    @Override
    public boolean isDeferred(Class<?> id) {
        return callbacks.containsKey(id);
    }

    @Override
    public boolean isPublished(Class<?> id) {
        return Boolean.TRUE.equals(published.get(id));
    }

    @Override
    public void publish(Class<?> id) {
        Consumer<ContainerContract> callback = callbacks.get(id);

        if (callback == null) {
            return;
        }

        callback.accept(this);

        published.put(id, true);
    }
}
