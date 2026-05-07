/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager.abstract_;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.manager.contract.ProvidersAwareContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.container.throwable.exception.ContainerInvalidPublishCallbackException;
import io.valkyrja.reflection.support.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ProvidersAware implements ProvidersAwareContract, ContainerContract {

    /** service type → owning provider (deferred) */
    protected final Map<Class<?>, ServiceProviderContract> deferred = new HashMap<>();

    /** service type → publish callback */
    protected final Map<Class<?>, Consumer<ContainerContract>> deferredCallbacks = new HashMap<>();

    /** service type → published flag */
    protected final Map<Class<?>, Boolean> published = new HashMap<>();

    /** provider identity → registered flag (using identity hash to avoid equals issues) */
    protected final Map<Class<?>, Boolean> registeredProviders = new HashMap<>();

    /** all registered providers in registration order */
    protected final List<Class<? extends ServiceProviderContract>> providers = new ArrayList<>();

    @Override
    public void register(Class<? extends ServiceProviderContract> providerClass) {
        if (isRegistered(providerClass)) {
            return;
        }

        providers.add(providerClass);

        registerDeferred(providerClass);
    }

    @Override
    public boolean isDeferred(Class<?> id) {
        return deferred.containsKey(id) || deferredCallbacks.containsKey(id);
    }

    @Override
    public boolean isPublished(Class<?> id) {
        return Boolean.TRUE.equals(published.get(id));
    }

    @Override
    public boolean isRegistered(Class<? extends ServiceProviderContract> provider) {
        return Boolean.TRUE.equals(registeredProviders.get(provider));
    }

    @Override
    public void publish(Class<?> id) {
        if (!deferred.containsKey(id)) {
            return;
        }

        Consumer<ContainerContract> callback = deferredCallbacks.get(id);

        callback.accept(this);

        published.put(id, true);
    }

    protected void registerDeferred(Class<? extends ServiceProviderContract> providerClass) {

        ServiceProviderContract provider = Reflection.instantiate(providerClass);

        Map<Class<?>, Consumer<ContainerContract>> publishers = provider.publishers();

        for (var publisher : publishers.entrySet()) {
            Class<?> provided = publisher.getKey();
            Consumer<ContainerContract> callback = publisher.getValue();

            if (callback == null) {
                throw new ContainerInvalidPublishCallbackException(
                        provided.getName() + " should have a valid publisher callback");
            }

            deferred.put(provided, provider);
            deferredCallbacks.put(provided, callback);
        }

        registeredProviders.put(provider.getClass(), true);
    }
}
