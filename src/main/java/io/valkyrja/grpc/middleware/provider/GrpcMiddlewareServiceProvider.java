/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.provider;

import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.grpc.middleware.handler.CallReceivedHandler;
import io.valkyrja.grpc.middleware.handler.ResponseSentHandler;
import io.valkyrja.grpc.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteMatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.grpc.middleware.handler.SendingResponseHandler;
import io.valkyrja.grpc.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Publishes the seven gRPC stage handlers as container singletons, each seeded with the middleware
 * from {@link GrpcConfigContract}. Because they are singletons, the {@code Router} and {@code
 * ServiceHandler} resolve the same instances, so per-route middleware registered onto them fires.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GrpcMiddlewareServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                CallReceivedHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishCallReceivedHandler,
                RouteMatchedHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishRouteMatchedHandler,
                RouteNotMatchedHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishRouteNotMatchedHandler,
                RouteDispatchedHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishRouteDispatchedHandler,
                ThrowableCaughtHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishThrowableCaughtHandler,
                SendingResponseHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishSendingResponseHandler,
                ResponseSentHandlerContract.class,
                        GrpcMiddlewareServiceProvider::publishResponseSentHandler);
    }

    public static void publishCallReceivedHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                CallReceivedHandlerContract.class,
                new CallReceivedHandler(
                        container, config.callReceivedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteMatchedHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                RouteMatchedHandlerContract.class,
                new RouteMatchedHandler(
                        container, config.routeMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteNotMatchedHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                RouteNotMatchedHandlerContract.class,
                new RouteNotMatchedHandler(
                        container, config.routeNotMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteDispatchedHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                RouteDispatchedHandlerContract.class,
                new RouteDispatchedHandler(
                        container, config.routeDispatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishThrowableCaughtHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                ThrowableCaughtHandlerContract.class,
                new ThrowableCaughtHandler(
                        container, config.throwableCaughtMiddleware().toArray(new Class[0])));
    }

    public static void publishSendingResponseHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                SendingResponseHandlerContract.class,
                new SendingResponseHandler(
                        container, config.sendingResponseMiddleware().toArray(new Class[0])));
    }

    public static void publishResponseSentHandler(ContainerContract container) {
        GrpcConfigContract config = container.getSingleton(GrpcConfigContract.class);
        container.setSingleton(
                ResponseSentHandlerContract.class,
                new ResponseSentHandler(
                        container, config.responseSentMiddleware().toArray(new Class[0])));
    }
}
