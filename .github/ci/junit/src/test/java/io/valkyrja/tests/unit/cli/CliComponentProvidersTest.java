/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.middleware.provider.CliMiddlewareComponentProvider;
import io.valkyrja.cli.middleware.provider.CliMiddlewareServiceProvider;
import io.valkyrja.cli.routing.provider.CliRoutingCliRouteProvider;
import io.valkyrja.cli.routing.provider.CliRoutingComponentProvider;
import io.valkyrja.cli.routing.provider.CliRoutingServiceProvider;
import io.valkyrja.cli.server.provider.CliServerComponentProvider;
import io.valkyrja.cli.server.provider.CliServerServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the cli component providers (middleware, routing, server). */
final class CliComponentProvidersTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void middlewareComponentProvider() {
        var provider = new CliMiddlewareComponentProvider();

        assertEquals(1, provider.getContainerProviders(app).size());
        assertTrue(
                provider.getContainerProviders(app).get(0) instanceof CliMiddlewareServiceProvider);
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void routingComponentProvider() {
        var provider = new CliRoutingComponentProvider();

        assertTrue(provider.getContainerProviders(app).get(0) instanceof CliRoutingServiceProvider);
        assertEquals(1, provider.getCliProviders(app).size());
        assertTrue(provider.getCliProviders(app).get(0) instanceof CliRoutingCliRouteProvider);
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void serverComponentProvider() {
        var provider = new CliServerComponentProvider();

        assertTrue(provider.getContainerProviders(app).get(0) instanceof CliServerServiceProvider);
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}
