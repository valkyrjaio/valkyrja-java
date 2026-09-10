/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.caster.Caster;
import io.valkyrja.cli.routing.caster.contract.CasterContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.Router;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CliRoutingServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                RouterContract.class,
                CliRoutingServiceProvider::publishRouter,
                RouteCollectionContract.class,
                CliRoutingServiceProvider::publishRouteCollection,
                CasterContract.class,
                CliRoutingServiceProvider::publishCaster);
    }

    public static void publishCaster(ContainerContract container) {
        container.setSingleton(CasterContract.class, new Caster(container));
    }

    public static void publishRouter(ContainerContract container) {
        container.setSingleton(
                RouterContract.class,
                new Router(
                        container,
                        container.getSingleton(RouteCollectionContract.class),
                        container.getSingleton(OutputFactoryContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(RouteMatchedHandlerContract.class),
                        container.getSingleton(RouteNotMatchedHandlerContract.class),
                        container.getSingleton(RouteDispatchedHandlerContract.class),
                        container.getSingleton(ProcessExitingHandlerContract.class)));
    }

    public static void publishRouteCollection(ContainerContract container) {
        RouteCollection collection = new RouteCollection();
        container.setSingleton(RouteCollectionContract.class, collection);

        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        List<Class<?>> controllers = new ArrayList<>();
        List<RouteContract> routes = new ArrayList<>();

        for (CliRouteProviderContract provider : app.getCliProviders()) {
            controllers.addAll(provider.getControllerClasses());
            routes.addAll(provider.getRoutes());
        }

        if (!controllers.isEmpty() && container.isSingleton(RouteCollectorContract.class)) {
            RouteCollectorContract collector = container.getSingleton(RouteCollectorContract.class);
            collection.add(
                    collector
                            .getRoutes(controllers.toArray(new Class[0]))
                            .toArray(new RouteContract[0]));
        }

        collection.add(routes.toArray(new RouteContract[0]));
    }
}
