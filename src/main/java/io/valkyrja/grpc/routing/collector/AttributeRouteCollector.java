/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.collector;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.routing.attribute.GrpcMethod;
import io.valkyrja.grpc.routing.attribute.GrpcMiddleware;
import io.valkyrja.grpc.routing.attribute.GrpcService;
import io.valkyrja.grpc.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Builds the service map by reflecting over {@link GrpcService}-annotated controller classes: each
 * {@link GrpcMethod} method becomes a {@code Route} keyed by {@code /service/name}, with the method
 * itself wired as the reflective handler and any {@link GrpcMiddleware} dispatched to its stage.
 */
public class AttributeRouteCollector implements RouteCollectorContract {

    @Override
    public List<RouteContract> getRoutes(Class<?>... controllerClasses) {
        List<RouteContract> routes = new ArrayList<>();

        for (Class<?> clazz : controllerClasses) {
            GrpcService service = clazz.getAnnotation(GrpcService.class);
            if (service == null) {
                continue;
            }

            for (Method method : clazz.getMethods()) {
                GrpcMethod grpcMethod = method.getAnnotation(GrpcMethod.class);
                if (grpcMethod == null) {
                    continue;
                }

                routes.add(buildRoute(service, grpcMethod, clazz, method));
            }
        }

        return routes;
    }

    protected RouteContract buildRoute(
            GrpcService service, GrpcMethod grpcMethod, Class<?> clazz, Method method) {
        String fullMethod = "/" + service.service() + "/" + grpcMethod.name();

        RouteContract route =
                new Route(fullMethod, handlerFor(clazz, method))
                        .withClientStreaming(grpcMethod.clientStreaming())
                        .withServerStreaming(grpcMethod.serverStreaming());

        return applyMiddleware(route, method);
    }

    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handlerFor(
            Class<?> clazz, Method method) {
        return (container, route) -> {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                return (ServiceResponseContract) method.invoke(instance, container, route);
            } catch (InvocationTargetException e) {
                // Surface the handler's own throwable (e.g. a framework-thrown CancelledException)
                // rather than the reflection wrapper, so ServiceHandler can map it to the correct
                // status instead of a blanket INTERNAL.
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (cause instanceof Error error) {
                    throw error;
                }
                throw new RuntimeException(cause != null ? cause : e);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected RouteContract applyMiddleware(RouteContract route, Method method) {
        GrpcMiddleware[] middlewares = method.getAnnotationsByType(GrpcMiddleware.class);

        for (GrpcMiddleware middleware : middlewares) {
            Class<?> middlewareClass = middleware.name();

            if (RouteMatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedRouteMatchedMiddleware(
                                List.of(
                                        (Class<? extends RouteMatchedMiddlewareContract>)
                                                middlewareClass));
            }

            if (RouteDispatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedRouteDispatchedMiddleware(
                                List.of(
                                        (Class<? extends RouteDispatchedMiddlewareContract>)
                                                middlewareClass));
            }

            if (ThrowableCaughtMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedThrowableCaughtMiddleware(
                                List.of(
                                        (Class<? extends ThrowableCaughtMiddlewareContract>)
                                                middlewareClass));
            }

            if (SendingResponseMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedSendingResponseMiddleware(
                                List.of(
                                        (Class<? extends SendingResponseMiddlewareContract>)
                                                middlewareClass));
            }

            if (TerminatedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedTerminatedMiddleware(
                                List.of(
                                        (Class<? extends TerminatedMiddlewareContract>)
                                                middlewareClass));
            }
        }

        return route;
    }
}
