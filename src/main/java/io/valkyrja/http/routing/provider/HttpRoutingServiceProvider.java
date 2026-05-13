/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.collector.AttributeRouteCollector;
import io.valkyrja.http.routing.collector.contract.RouteCollectorContract;
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
                RouterContract.class,                 HttpRoutingServiceProvider::publishRouter,
                RouteCollectionContract.class,        HttpRoutingServiceProvider::publishRouteCollection,
                MatcherContract.class,                HttpRoutingServiceProvider::publishMatcher,
                UrlContract.class,                    HttpRoutingServiceProvider::publishUrl,
                RouteCollectorContract.class,         HttpRoutingServiceProvider::publishAttributesRouteCollector,
                ProcessorContract.class,              HttpRoutingServiceProvider::publishProcessor,
                RoutingResponseFactoryContract.class, HttpRoutingServiceProvider::publishResponseFactory);
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
                        container.getSingleton(TerminatedHandlerContract.class)));
    }

    public static void publishRouteCollection(ContainerContract container) {
        RouteCollection collection = new RouteCollection();
        container.setSingleton(RouteCollectionContract.class, collection);

        ApplicationContract app = container.getSingleton(ApplicationContract.class);
        ProcessorContract processor = container.getSingleton(ProcessorContract.class);

        List<Class<?>> controllers = new ArrayList<>();
        List<RouteContract> routes = new ArrayList<>();

        for (HttpRouteProviderContract provider :
                app.getHttpProviders().stream()
                        .map(c -> (HttpRouteProviderContract) container.get(c))
                        .toList()) {
            controllers.addAll(provider.getControllerClasses());
            routes.addAll(provider.getRoutes());
        }

        if (!controllers.isEmpty() && container.isSingleton(RouteCollectorContract.class)) {
            RouteCollectorContract collector = container.getSingleton(RouteCollectorContract.class);
            for (RouteContract route : collector.getRoutes(controllers.toArray(new Class[0]))) {
                collection.add(route);
            }
        }

        for (RouteContract route : routes) {
            collection.add(processor.route(route));
        }
    }

    public static void publishMatcher(ContainerContract container) {
        container.setSingleton(
                MatcherContract.class,
                new Matcher(container.getSingleton(RouteCollectionContract.class)));
    }

    public static void publishUrl(ContainerContract container) {
        container.setSingleton(
                UrlContract.class,
                new Url(container.getSingleton(RouteCollectionContract.class)));
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