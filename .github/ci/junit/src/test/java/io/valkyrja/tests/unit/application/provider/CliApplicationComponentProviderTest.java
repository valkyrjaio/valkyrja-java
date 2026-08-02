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
import io.valkyrja.application.provider.CliApplicationComponentProvider;
import io.valkyrja.cli.interaction.provider.CliInteractionComponentProvider;
import io.valkyrja.cli.middleware.provider.CliMiddlewareComponentProvider;
import io.valkyrja.cli.routing.provider.CliRoutingComponentProvider;
import io.valkyrja.cli.server.provider.CliServerComponentProvider;
import io.valkyrja.log.provider.LogComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link CliApplicationComponentProvider}. */
final class CliApplicationComponentProviderTest {

    private final CliApplicationComponentProvider provider = new CliApplicationComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersReturnsTheComponentProviders() {
        var providers = provider.getComponentProviders(app);

        assertEquals(6, providers.size());
        assertInstanceOf(ApplicationComponentProvider.class, providers.get(0));
        assertInstanceOf(CliInteractionComponentProvider.class, providers.get(1));
        assertInstanceOf(CliMiddlewareComponentProvider.class, providers.get(2));
        assertInstanceOf(CliRoutingComponentProvider.class, providers.get(3));
        assertInstanceOf(CliServerComponentProvider.class, providers.get(4));
        assertInstanceOf(LogComponentProvider.class, providers.get(5));
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
