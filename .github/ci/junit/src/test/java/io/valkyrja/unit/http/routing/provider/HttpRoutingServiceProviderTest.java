/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.routing.matcher.contract.MatcherContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.processor.contract.ProcessorContract;
import io.valkyrja.http.routing.provider.HttpRoutingServiceProvider;
import io.valkyrja.http.routing.factory.contract.RoutingResponseFactoryContract;
import io.valkyrja.http.routing.url.contract.UrlContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingServiceProvider}. */
final class HttpRoutingServiceProviderTest {

    @Test
    void publishersExposeAllRoutingServices() {
        assertEquals(7, new HttpRoutingServiceProvider().publishers().size());
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
        container.setSingleton(ResponseSentHandlerContract.class, mock(ResponseSentHandlerContract.class));
        container.setSingleton(UrlContract.class, mock(UrlContract.class));

        HttpRoutingServiceProvider.publishRouter(container);
        HttpRoutingServiceProvider.publishResponseFactory(container);

        assertInstanceOf(RouterContract.class, container.getSingleton(RouterContract.class));
        assertInstanceOf(
                RoutingResponseFactoryContract.class,
                container.getSingleton(RoutingResponseFactoryContract.class));
    }

    @Test
    void publishRouteCollectionWithCollectorAndProviderRoutes() {
        var container = new Container();
        container.setSingleton(ProcessorContract.class, new Processor());
        var collector = mock(RouteCollectorContract.class);
        when(collector.getRoutes(any()))
                .thenReturn(List.<RouteContract>of(route("generated", "/gen")));
        container.setSingleton(RouteCollectorContract.class, collector);

        var app = mock(ApplicationContract.class);
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
    void publishRouteCollectionWithoutControllersSkipsCollector() {
        var container = new Container();
        container.setSingleton(ProcessorContract.class, new Processor());

        var app = mock(ApplicationContract.class);
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
    void publishRouteCollectionWithControllersButNoCollectorSkipsCollection() {
        var container = new Container();
        container.setSingleton(ProcessorContract.class, new Processor());
        // No RouteCollectorContract singleton is registered.
        var app = mock(ApplicationContract.class);
        var routeProvider = mock(HttpRouteProviderContract.class);
        when(routeProvider.getControllerClasses()).thenReturn(List.of(Object.class));
        when(routeProvider.getRoutes()).thenReturn(List.of(route("direct", "/direct")));
        when(app.getHttpProviders()).thenReturn(List.of(routeProvider));
        container.setSingleton(ApplicationContract.class, app);

        HttpRoutingServiceProvider.publishRouteCollection(container);

        var collection = container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.hasName("direct"));
    }

}
