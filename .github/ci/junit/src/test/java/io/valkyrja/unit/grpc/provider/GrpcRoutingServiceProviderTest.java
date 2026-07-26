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
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.routing.collector.AttributeRouteCollector;
import io.valkyrja.grpc.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.provider.GrpcRoutingServiceProvider;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.fixtures.grpc.GreeterController;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcRoutingServiceProvider} route-collection assembly branches. */
final class GrpcRoutingServiceProviderTest {

    private ContainerContract containerWith(GrpcRouteProviderContract provider) {
        ApplicationContract app = mock(ApplicationContract.class);
        when(app.getGrpcProviders()).thenReturn(List.of(provider));

        ContainerContract container = new Container();
        container.setSingleton(ApplicationContract.class, app);
        return container;
    }

    private GrpcRouteProviderContract provider(List<Class<?>> controllers, List<RouteContract> routes) {
        return new GrpcRouteProviderContract() {
            @Override
            public List<Class<?>> getControllerClasses() {
                return controllers;
            }

            @Override
            public List<RouteContract> getRoutes() {
                return routes;
            }
        };
    }

    @Test
    void collectsControllersWhenACollectorIsRegistered() {
        ContainerContract container =
                containerWith(provider(List.of(GreeterController.class), List.of()));
        container.setSingleton(RouteCollectorContract.class, new AttributeRouteCollector());

        GrpcRoutingServiceProvider.publishRouteCollection(container);

        RouteCollectionContract collection =
                container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.has("/pkg.Greeter/SayHello"));
    }

    @Test
    void skipsControllersWhenNoCollectorIsRegistered() {
        ContainerContract container =
                containerWith(provider(List.of(GreeterController.class), List.of()));

        GrpcRoutingServiceProvider.publishRouteCollection(container);

        RouteCollectionContract collection =
                container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.all().isEmpty());
    }

    @Test
    void addsPreBuiltRoutesWhenNoControllers() {
        RouteContract route = new Route("/pkg.A/M", (c, r) -> ServiceResponse.ok());
        ContainerContract container = containerWith(provider(List.of(), List.of(route)));

        GrpcRoutingServiceProvider.publishRouteCollection(container);

        RouteCollectionContract collection =
                container.getSingleton(RouteCollectionContract.class);
        assertEquals(1, collection.all().size());
        assertTrue(collection.has("/pkg.A/M"));
    }

    @Test
    void publishRouteCollectorRegistersAttributeCollector() {
        ContainerContract container = new Container();
        GrpcRoutingServiceProvider.publishRouteCollector(container);
        assertTrue(
                container.getSingleton(RouteCollectorContract.class)
                        instanceof AttributeRouteCollector);
    }
}
