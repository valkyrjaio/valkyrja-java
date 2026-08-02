/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.HttpApplicationComponentProvider;
import io.valkyrja.http.message.provider.HttpMessageComponentProvider;
import io.valkyrja.http.middleware.provider.HttpMiddlewareComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingCliComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingComponentProvider;
import io.valkyrja.http.server.provider.HttpServerComponentProvider;
import io.valkyrja.log.provider.LogComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpApplicationComponentProvider}. */
final class HttpApplicationComponentProviderTest {

    private final HttpApplicationComponentProvider provider =
            new HttpApplicationComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersReturnsTheComponentProviders() {
        var providers = provider.getComponentProviders(app);

        assertEquals(7, providers.size());
        assertInstanceOf(ApplicationComponentProvider.class, providers.get(0));
        assertInstanceOf(HttpMessageComponentProvider.class, providers.get(1));
        assertInstanceOf(HttpMiddlewareComponentProvider.class, providers.get(2));
        assertInstanceOf(HttpRoutingComponentProvider.class, providers.get(3));
        assertInstanceOf(HttpRoutingCliComponentProvider.class, providers.get(4));
        assertInstanceOf(HttpServerComponentProvider.class, providers.get(5));
        assertInstanceOf(LogComponentProvider.class, providers.get(6));
    }

    @Test
    void getContainerProvidersIsEmpty() {
        assertTrue(provider.getContainerProviders(app).isEmpty());
    }

    @Test
    void getEventProvidersIsEmpty() {
        assertTrue(provider.getEventProviders(app).isEmpty());
    }

    @Test
    void getCliProvidersIsEmpty() {
        assertTrue(provider.getCliProviders(app).isEmpty());
    }

    @Test
    void getHttpProvidersIsEmpty() {
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void getGrpcProvidersIsEmpty() {
        assertTrue(provider.getGrpcProviders(app).isEmpty());
    }
}
