/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.provider;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.http.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.http.middleware.handler.RouteMatchedHandler;
import io.valkyrja.http.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.TerminatedHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class HttpMiddlewareServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                RequestReceivedHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishRequestReceivedHandler,
                ThrowableCaughtHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishThrowableCaughtHandler,
                RouteMatchedHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishRouteMatchedHandler,
                RouteNotMatchedHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishRouteNotMatchedHandler,
                RouteDispatchedHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishRouteDispatchedHandler,
                SendingResponseHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishSendingResponseHandler,
                TerminatedHandlerContract.class,
                        HttpMiddlewareServiceProvider::publishTerminatedHandler);
    }

    public static void publishRequestReceivedHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                RequestReceivedHandlerContract.class,
                new RequestReceivedHandler(
                        container, config.requestReceivedMiddleware().toArray(new Class[0])));
    }

    public static void publishThrowableCaughtHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                ThrowableCaughtHandlerContract.class,
                new ThrowableCaughtHandler(
                        container, config.throwableCaughtMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteMatchedHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                RouteMatchedHandlerContract.class,
                new RouteMatchedHandler(
                        container, config.routeMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteNotMatchedHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                RouteNotMatchedHandlerContract.class,
                new RouteNotMatchedHandler(
                        container, config.routeNotMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteDispatchedHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                RouteDispatchedHandlerContract.class,
                new RouteDispatchedHandler(
                        container, config.routeDispatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishSendingResponseHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                SendingResponseHandlerContract.class,
                new SendingResponseHandler(
                        container, config.sendingResponseMiddleware().toArray(new Class[0])));
    }

    public static void publishTerminatedHandler(ContainerContract container) {
        HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
        container.setSingleton(
                TerminatedHandlerContract.class,
                new TerminatedHandler(
                        container, config.terminatedMiddleware().toArray(new Class[0])));
    }
}
