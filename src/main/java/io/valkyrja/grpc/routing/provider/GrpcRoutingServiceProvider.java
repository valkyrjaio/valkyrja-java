/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.collection.RouteCollection;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.routing.collector.AttributeRouteCollector;
import io.valkyrja.grpc.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.dispatcher.Router;
import io.valkyrja.grpc.routing.dispatcher.contract.RouterContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Publishes the gRPC {@code Router}, service map, and attribute collector into the container. */
public class GrpcRoutingServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                RouterContract.class, GrpcRoutingServiceProvider::publishRouter,
                RouteCollectionContract.class, GrpcRoutingServiceProvider::publishRouteCollection,
                RouteCollectorContract.class, GrpcRoutingServiceProvider::publishRouteCollector);
    }

    public static void publishRouter(ContainerContract container) {
        container.setSingleton(
                RouterContract.class,
                new Router(
                        container,
                        container.getSingleton(RouteCollectionContract.class),
                        container.getSingleton(RouteMatchedHandlerContract.class),
                        container.getSingleton(RouteNotMatchedHandlerContract.class),
                        container.getSingleton(RouteDispatchedHandlerContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(SendingResponseHandlerContract.class),
                        container.getSingleton(TerminatedHandlerContract.class)));
    }

    public static void publishRouteCollection(ContainerContract container) {
        RouteCollection collection = new RouteCollection();
        container.setSingleton(RouteCollectionContract.class, collection);

        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        List<Class<?>> controllers = new ArrayList<>();
        List<RouteContract> routes = new ArrayList<>();

        for (GrpcRouteProviderContract provider : app.getGrpcProviders()) {
            controllers.addAll(provider.getControllerClasses());
            routes.addAll(provider.getRoutes());
        }

        if (!controllers.isEmpty() && container.has(RouteCollectorContract.class)) {
            RouteCollectorContract collector = container.getSingleton(RouteCollectorContract.class);
            collection.add(
                    collector
                            .getRoutes(controllers.toArray(new Class[0]))
                            .toArray(new RouteContract[0]));
        }

        collection.add(routes.toArray(new RouteContract[0]));
    }

    public static void publishRouteCollector(ContainerContract container) {
        container.setSingleton(RouteCollectorContract.class, new AttributeRouteCollector());
    }
}
