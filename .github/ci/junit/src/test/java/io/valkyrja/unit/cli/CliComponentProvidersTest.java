/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.middleware.provider.CliMiddlewareComponentProvider;
import io.valkyrja.cli.routing.provider.CliRoutingCliRouteProvider;
import io.valkyrja.cli.routing.provider.CliRoutingComponentProvider;
import io.valkyrja.cli.routing.provider.CliRoutingServiceProvider;
import io.valkyrja.cli.server.provider.CliServerComponentProvider;
import io.valkyrja.cli.server.provider.CliServerServiceProvider;
import io.valkyrja.cli.middleware.provider.CliMiddlewareServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the cli component providers (middleware, routing, server). */
final class CliComponentProvidersTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void middlewareComponentProvider() {
        var provider = new CliMiddlewareComponentProvider();

        assertEquals(1, provider.getContainerProviders(app).size());
        assertTrue(provider.getContainerProviders(app).get(0) instanceof CliMiddlewareServiceProvider);
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