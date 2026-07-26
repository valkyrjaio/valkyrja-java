/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.GrpcApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareComponentProvider;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareServiceProvider;
import io.valkyrja.grpc.routing.provider.GrpcRoutingComponentProvider;
import io.valkyrja.grpc.routing.provider.GrpcRoutingServiceProvider;
import io.valkyrja.grpc.server.provider.GrpcServerComponentProvider;
import io.valkyrja.grpc.server.provider.GrpcServerServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the gRPC component providers (middleware, routing, server, application). */
final class GrpcComponentProvidersTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    private void assertOnlyContainerProvider(ComponentProviderContract provider) {
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
        assertTrue(provider.getGrpcProviders(app).isEmpty());
    }

    @Test
    void middlewareComponentProvider() {
        GrpcMiddlewareComponentProvider provider = new GrpcMiddlewareComponentProvider();
        assertEquals(1, provider.getContainerProviders(app).size());
        assertTrue(
                provider.getContainerProviders(app).get(0)
                        instanceof GrpcMiddlewareServiceProvider);
        assertOnlyContainerProvider(provider);
    }

    @Test
    void routingComponentProvider() {
        GrpcRoutingComponentProvider provider = new GrpcRoutingComponentProvider();
        assertTrue(
                provider.getContainerProviders(app).get(0) instanceof GrpcRoutingServiceProvider);
        assertOnlyContainerProvider(provider);
    }

    @Test
    void serverComponentProvider() {
        GrpcServerComponentProvider provider = new GrpcServerComponentProvider();
        assertTrue(provider.getContainerProviders(app).get(0) instanceof GrpcServerServiceProvider);
        assertOnlyContainerProvider(provider);
    }

    @Test
    void applicationComponentProviderAggregatesTheStack() {
        GrpcApplicationComponentProvider provider = new GrpcApplicationComponentProvider();
        var components = provider.getComponentProviders(app);

        assertEquals(4, components.size());
        assertTrue(components.get(0) instanceof ApplicationComponentProvider);
        assertTrue(components.get(1) instanceof GrpcMiddlewareComponentProvider);
        assertTrue(components.get(2) instanceof GrpcRoutingComponentProvider);
        assertTrue(components.get(3) instanceof GrpcServerComponentProvider);

        assertTrue(provider.getContainerProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
        assertTrue(provider.getGrpcProviders(app).isEmpty());
    }
}
