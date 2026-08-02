/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.collector.AttributeRouteCollector;
import io.valkyrja.http.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.http.routing.data.HttpRoutingData;
import io.valkyrja.http.routing.data.contract.HttpRoutingDataContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.dispatcher.Router;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.routing.factory.RoutingResponseFactory;
import io.valkyrja.http.routing.factory.contract.RoutingResponseFactoryContract;
import io.valkyrja.http.routing.matcher.Matcher;
import io.valkyrja.http.routing.matcher.contract.MatcherContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.processor.contract.ProcessorContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import io.valkyrja.http.routing.url.Url;
import io.valkyrja.http.routing.url.contract.UrlContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HttpRoutingServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                RouterContract.class, HttpRoutingServiceProvider::publishRouter,
                RouteCollectionContract.class, HttpRoutingServiceProvider::publishRouteCollection,
                MatcherContract.class, HttpRoutingServiceProvider::publishMatcher,
                UrlContract.class, HttpRoutingServiceProvider::publishUrl,
                RouteCollectorContract.class,
                        HttpRoutingServiceProvider::publishAttributesRouteCollector,
                ProcessorContract.class, HttpRoutingServiceProvider::publishProcessor,
                RoutingResponseFactoryContract.class,
                        HttpRoutingServiceProvider::publishResponseFactory,
                HttpRoutingDataContract.class, HttpRoutingServiceProvider::publishData);
    }

    public static void publishRouter(ContainerContract container) {
        container.setSingleton(
                RouterContract.class,
                new Router(
                        container,
                        container.getSingleton(MatcherContract.class),
                        container.getSingleton(ResponseFactoryContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(RouteMatchedHandlerContract.class),
                        container.getSingleton(RouteNotMatchedHandlerContract.class),
                        container.getSingleton(RouteDispatchedHandlerContract.class),
                        container.getSingleton(SendingResponseHandlerContract.class),
                        container.getSingleton(ResponseSentHandlerContract.class)));
    }

    public static void publishRouteCollection(ContainerContract container) {
        RouteCollection collection = new RouteCollection();
        container.setSingleton(RouteCollectionContract.class, collection);

        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        // In debug mode the routes are collected from the providers and their controllers'
        // annotations on every boot. Otherwise they are loaded from the generated routing data,
        // whose handlers are direct method references (no controller reflection required).
        if (app.getDebugMode()) {
            publishData(container);

            return;
        }

        HttpRoutingDataContract data = container.getSingleton(HttpRoutingDataContract.class);

        collection.setFromData(
                new HttpRoutingData(
                        data.routes(), data.paths(), data.dynamicPaths(), data.regexes()));
    }

    /**
     * Collect the routes from the application's HTTP route providers.
     *
     * <p>Gathers every provider's annotated controllers and explicitly declared routes, adds them
     * to the published route collection, and publishes the resulting routing data. Doubling as the
     * {@link HttpRoutingDataContract} publisher means an application that ships no generated
     * routing data still resolves a fully collected data set on demand.
     *
     * @param container the container
     */
    public static void publishData(ContainerContract container) {
        RouteCollectionContract collection = container.getSingleton(RouteCollectionContract.class);
        ApplicationContract app = container.getSingleton(ApplicationContract.class);
        ProcessorContract processor = container.getSingleton(ProcessorContract.class);

        List<Class<?>> controllers = new ArrayList<>();
        List<RouteContract> routes = new ArrayList<>();

        for (HttpRouteProviderContract provider : app.getHttpProviders()) {
            controllers.addAll(provider.getControllerClasses());
            routes.addAll(provider.getRoutes());
        }

        if (!controllers.isEmpty()) {
            RouteCollectorContract collector = container.getSingleton(RouteCollectorContract.class);
            for (RouteContract route : collector.getRoutes(controllers.toArray(new Class[0]))) {
                collection.add(route);
            }
        }

        for (RouteContract route : routes) {
            collection.add(processor.route(route));
        }

        container.setSingleton(HttpRoutingDataContract.class, collection.getData());
    }

    public static void publishMatcher(ContainerContract container) {
        container.setSingleton(
                MatcherContract.class,
                new Matcher(container.getSingleton(RouteCollectionContract.class)));
    }

    public static void publishUrl(ContainerContract container) {
        container.setSingleton(
                UrlContract.class, new Url(container.getSingleton(RouteCollectionContract.class)));
    }

    public static void publishAttributesRouteCollector(ContainerContract container) {
        container.setSingleton(
                RouteCollectorContract.class,
                new AttributeRouteCollector(container.getSingleton(ProcessorContract.class)));
    }

    public static void publishProcessor(ContainerContract container) {
        container.setSingleton(ProcessorContract.class, new Processor());
    }

    public static void publishResponseFactory(ContainerContract container) {
        container.setSingleton(
                RoutingResponseFactoryContract.class,
                new RoutingResponseFactory(
                        container.getSingleton(ResponseFactoryContract.class),
                        container.getSingleton(UrlContract.class)));
    }
}
