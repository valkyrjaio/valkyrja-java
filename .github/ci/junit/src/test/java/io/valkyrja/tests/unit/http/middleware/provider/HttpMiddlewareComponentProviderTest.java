/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.middleware.provider.HttpMiddlewareComponentProvider;
import io.valkyrja.http.middleware.provider.HttpMiddlewareServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMiddlewareComponentProvider}. */
final class HttpMiddlewareComponentProviderTest {

    private final HttpMiddlewareComponentProvider provider = new HttpMiddlewareComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersIsEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
    }

    @Test
    void getContainerProvidersReturnsTheServiceProviders() {
        var providers = provider.getContainerProviders(app);

        assertEquals(1, providers.size());
        assertInstanceOf(HttpMiddlewareServiceProvider.class, providers.get(0));
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
