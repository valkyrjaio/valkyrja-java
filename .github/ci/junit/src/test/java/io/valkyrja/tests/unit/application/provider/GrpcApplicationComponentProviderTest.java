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
import io.valkyrja.application.provider.GrpcApplicationComponentProvider;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareComponentProvider;
import io.valkyrja.grpc.routing.provider.GrpcRoutingComponentProvider;
import io.valkyrja.grpc.server.provider.GrpcServerComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcApplicationComponentProvider}. */
final class GrpcApplicationComponentProviderTest {

    private final GrpcApplicationComponentProvider provider =
            new GrpcApplicationComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersReturnsTheComponentProviders() {
        var providers = provider.getComponentProviders(app);

        assertEquals(4, providers.size());
        assertInstanceOf(ApplicationComponentProvider.class, providers.get(0));
        assertInstanceOf(GrpcMiddlewareComponentProvider.class, providers.get(1));
        assertInstanceOf(GrpcRoutingComponentProvider.class, providers.get(2));
        assertInstanceOf(GrpcServerComponentProvider.class, providers.get(3));
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
