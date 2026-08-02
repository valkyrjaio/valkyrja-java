/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.middleware.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareComponentProvider;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcMiddlewareComponentProvider}. */
final class GrpcMiddlewareComponentProviderTest {

    private final GrpcMiddlewareComponentProvider provider = new GrpcMiddlewareComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersIsEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
    }

    @Test
    void getContainerProvidersReturnsTheServiceProviders() {
        var providers = provider.getContainerProviders(app);

        assertEquals(1, providers.size());
        assertInstanceOf(GrpcMiddlewareServiceProvider.class, providers.get(0));
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
