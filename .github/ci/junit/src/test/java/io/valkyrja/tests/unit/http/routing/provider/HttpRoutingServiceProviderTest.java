/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.http.routing.data.HttpRoutingData;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.HttpRoutingDataContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.routing.factory.contract.RoutingResponseFactoryContract;
import io.valkyrja.http.routing.matcher.contract.MatcherContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.processor.contract.ProcessorContract;
import io.valkyrja.http.routing.provider.HttpRoutingServiceProvider;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import io.valkyrja.http.routing.url.contract.UrlContract;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingServiceProvider}. */
final class HttpRoutingServiceProviderTest {

    @Test
    void publishersExposeAllRoutingServices() {
        assertEquals(8, new HttpRoutingServiceProvider().publishers().size());
    }

    @Test
    void publishStandaloneServices() {
        var container = new Container();
        container.setSingleton(RouteCollectionContract.class, new RouteCollection());

        HttpRoutingServiceProvider.publishProcessor(container);
        HttpRoutingServiceProvider.publishMatcher(container);
        HttpRoutingServiceProvider.publishUrl(container);
        HttpRoutingServiceProvider.publishAttributesRouteCollector(container);

        assertInstanceOf(ProcessorContract.class, container.getSingleton(ProcessorContract.class));
        assertInstanceOf(MatcherContract.class, container.getSingleton(MatcherContract.class));
        assertInstanceOf(UrlContract.class, container.getSingleton(UrlContract.class));
        assertInstanceOf(
                RouteCollectorContract.class, container.getSingleton(RouteCollectorContract.class));
    }

    @Test
    void publishRouterAndResponseFactory() {
        var container = new Container();
        container.setSingleton(MatcherContract.class, mock(MatcherContract.class));
        container.setSingleton(ResponseFactoryContract.class, new ResponseFactory());
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, mock(ThrowableCaughtHandlerContract.class));
        container.setSingleton(
                RouteMatchedHandlerContract.class, mock(RouteMatchedHandlerContract.class));
        container.setSingleton(
                RouteNotMatchedHandlerContract.class, mock(RouteNotMatchedHandlerContract.class));
        container.setSingleton(
                RouteDispatchedHandlerContract.class, mock(RouteDispatchedHandlerContract.class));
        container.setSingleton(
                SendingResponseHandlerContract.class, mock(SendingResponseHandlerContract.class));
        container.setSingleton(
                ResponseSentHandlerContract.class, mock(ResponseSentHandlerContract.class));
        container.setSingleton(UrlContract.class, mock(UrlContract.class));

        HttpRoutingServiceProvider.publishRouter(container);
        HttpRoutingServiceProvider.publishResponseFactory(container);

        assertInstanceOf(RouterContract.class, container.getSingleton(RouterContract.class));
        assertInstanceOf(
                RoutingResponseFactoryContract.class,
                container.getSingleton(RoutingResponseFactoryContract.class));
    }

    @Test
    void publishRouteCollectionInDebugModeCollectsFromProviders() {
        var container = new Container();
        container.setSingleton(ProcessorContract.class, new Processor());
        var collector = mock(RouteCollectorContract.class);
        when(collector.getRoutes(any()))
                .thenReturn(List.<RouteContract>of(route("generated", "/gen")));
        container.setSingleton(RouteCollectorContract.class, collector);

        var app = mock(ApplicationContract.class);
        when(app.getDebugMode()).thenReturn(true);
        var routeProvider = mock(HttpRouteProviderContract.class);
        when(routeProvider.getControllerClasses()).thenReturn(List.of(Object.class));
        when(routeProvider.getRoutes()).thenReturn(List.of(route("direct", "/direct")));
        when(app.getHttpProviders()).thenReturn(List.of(routeProvider));
        container.setSingleton(ApplicationContract.class, app);

        HttpRoutingServiceProvider.publishRouteCollection(container);

        var collection = container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.hasName("generated"));
        assertTrue(collection.hasName("direct"));
    }

    private static RouteContract route(String name, String path) {
        return new Route(path, name, (container, route) -> new EmptyResponse());
    }

    @Test
    void publishRouteCollectionInDebugModeWithoutControllersSkipsCollector() {
        var container = new Container();
        container.setSingleton(ProcessorContract.class, new Processor());

        var app = mock(ApplicationContract.class);
        when(app.getDebugMode()).thenReturn(true);
        var routeProvider = mock(HttpRouteProviderContract.class);
        when(routeProvider.getControllerClasses()).thenReturn(List.of());
        when(routeProvider.getRoutes()).thenReturn(List.of(route("direct", "/direct")));
        when(app.getHttpProviders()).thenReturn(List.of(routeProvider));
        container.setSingleton(ApplicationContract.class, app);

        HttpRoutingServiceProvider.publishRouteCollection(container);

        var collection = container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.hasName("direct"));
    }

    @Test
    void publishRouteCollectionOutsideDebugModeLoadsFromRoutingData() {
        var container = new Container();

        var app = mock(ApplicationContract.class);
        container.setSingleton(ApplicationContract.class, app);
        container.setSingleton(
                HttpRoutingDataContract.class,
                new HttpRoutingData(
                        Map.of("welcome", () -> route("welcome", "/")),
                        Map.of(RequestMethod.GET.getValue(), Map.of("/", "welcome")),
                        Map.of(),
                        Map.of()));

        HttpRoutingServiceProvider.publishRouteCollection(container);

        var collection = container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.hasName("welcome"));
        assertTrue(collection.hasPath("/", RequestMethod.GET));
    }
}
