/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager.abstract_;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.container.throwable.exception.ContainerInvalidPublishCallbackException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** Exercised through the concrete {@link Container}, which extends {@code ProvidersAware}. */
final class ProvidersAwareTest {

    private static ServiceProviderContract providerWith(
            Map<Class<?>, Consumer<ContainerContract>> publishers) {
        var provider = mock(ServiceProviderContract.class);
        when(provider.publishers()).thenReturn(publishers);
        return provider;
    }

    @Test
    void registerThenPublishInvokesCallbacksAndMarksPublished() {
        var container = new Container();
        var firstPublished = new AtomicBoolean(false);
        var secondPublished = new AtomicBoolean(false);

        Map<Class<?>, Consumer<ContainerContract>> publishers = new HashMap<>();
        publishers.put(String.class, c -> firstPublished.set(true));
        publishers.put(Integer.class, c -> secondPublished.set(true));

        var provider = providerWith(publishers);
        container.register(provider);
        // Re-registering the same provider just overwrites the callbacks.
        container.register(provider);

        assertFalse(firstPublished.get());
        assertFalse(container.isPublished(String.class));

        container.publish(String.class);

        assertTrue(firstPublished.get());
        assertTrue(container.isPublished(String.class));
        assertFalse(secondPublished.get());
        assertFalse(container.isPublished(Integer.class));

        container.publish(Integer.class);

        assertTrue(secondPublished.get());
        assertTrue(container.isPublished(Integer.class));
    }

    @Test
    void publishWithoutRegisteredCallbackIsNoOp() {
        var container = new Container();

        container.publish(String.class);

        assertFalse(container.isPublished(String.class));
    }

    @Test
    void registerNullCallbackThrows() {
        var container = new Container();
        Map<Class<?>, Consumer<ContainerContract>> publishers = new HashMap<>();
        publishers.put(String.class, null);
        var provider = providerWith(publishers);

        assertThrows(
                ContainerInvalidPublishCallbackException.class, () -> container.register(provider));
    }
}
