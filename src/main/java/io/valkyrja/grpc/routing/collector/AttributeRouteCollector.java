/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.collector;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.routing.attribute.Route;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.attribute.route.Middleware;
import io.valkyrja.grpc.routing.attribute.route.Handler;
import io.valkyrja.grpc.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidHandlerException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

public class AttributeRouteCollector implements RouteCollectorContract {

    /** The parameters every handler takes, whichever spelling of the handler named it. */
    private static final Class<?>[] HANDLER_PARAMETERS = {
        ContainerContract.class, RouteContract.class
    };

    @Override
    public List<RouteContract> getRoutes(Class<?>... controllerClasses) {
        List<RouteContract> routes = new ArrayList<>();

        for (Class<?> clazz : controllerClasses) {
            Service service = clazz.getAnnotation(Service.class);
            if (service == null) {
                continue;
            }

            for (Method method : clazz.getMethods()) {
                Route routeAttribute = method.getAnnotation(Route.class);
                if (routeAttribute == null) {
                    continue;
                }

                routes.add(buildRoute(service, routeAttribute, clazz, method));
            }
        }

        return routes;
    }

    protected RouteContract buildRoute(
            Service service, Route routeAttribute, Class<?> clazz, Method method) {
        String fullMethod = "/" + service.service() + "/" + routeAttribute.name();

        RouteContract route =
                new io.valkyrja.grpc.routing.data.Route(
                                fullMethod, buildHandler(routeAttribute, clazz, method))
                        .withRequestType(messageType(routeAttribute.requestType()))
                        .withResponseType(messageType(routeAttribute.responseType()))
                        .withClientStreaming(routeAttribute.clientStreaming())
                        .withServerStreaming(routeAttribute.serverStreaming())
                        .withRouteMatchedMiddleware(
                                List.of(routeAttribute.routeMatchedMiddleware()))
                        .withRouteDispatchedMiddleware(
                                List.of(routeAttribute.routeDispatchedMiddleware()))
                        .withThrowableCaughtMiddleware(
                                List.of(routeAttribute.throwableCaughtMiddleware()))
                        .withSendingResponseMiddleware(
                                List.of(routeAttribute.sendingResponseMiddleware()))
                        .withResponseSentMiddleware(
                                List.of(routeAttribute.responseSentMiddleware()));

        return applyMiddleware(route, method);
    }

    /**
     * Get the message type an element names, or null where it names the absent type.
     *
     * @param type the type the element names
     * @return the type, or null where the element names none
     */
    protected @Nullable Class<?> messageType(Class<?> type) {
        return type == Void.class ? null : type;
    }

    /**
     * Get the route's handler.
     *
     * @param routeAttribute the route attribute
     * @param clazz the controller class
     * @param method the annotated method
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> buildHandler(
            Route routeAttribute, Class<?> clazz, Method method) {
        Handler annotation = method.getAnnotation(Handler.class);

        // A single-valued field takes the convenience attribute's value, so the attribute wins
        // over the element. Either spelling names no handler when it names the absent type.
        if (annotation != null && annotation.handlerClass() != Void.class) {
            return namedHandler(annotation.handlerClass(), annotation.handlerMethod());
        }

        Handler element = routeAttribute.handler();

        if (element.handlerClass() != Void.class) {
            return namedHandler(element.handlerClass(), element.handlerMethod());
        }

        return controllerHandler(clazz, method);
    }

    /**
     * Get the handler a {@link Handler} names.
     *
     * @param handlerClass the class declaring the handler
     * @param handlerMethod the handler's method name
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> namedHandler(
            Class<?> handlerClass, String handlerMethod) {
        // A named handler is static, so a controller with constructor dependencies is reached
        // by the handler resolving it from the container.
        Method handler = resolveHandler(handlerClass, handlerMethod);

        return (container, route) -> invoke(handler, null, container, route);
    }

    /**
     * Get the method a {@link Handler} names.
     *
     * @param handlerClass the class declaring the handler
     * @param handlerMethod the handler's method name
     * @return the handler method
     */
    protected Method resolveHandler(Class<?> handlerClass, String handlerMethod) {
        // Java holds no annotation member to a signature, so the collection is the only place
        // left that can, and it runs once rather than once for each call.
        String named = handlerClass.getName() + "#" + handlerMethod;
        Method handler;

        try {
            handler =
                    handlerClass.getMethod(
                            handlerMethod, ContainerContract.class, RouteContract.class);
        } catch (NoSuchMethodException e) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `"
                            + named
                            + "` does not exist. A handler takes a ContainerContract and a"
                            + " RouteContract.",
                    e);
        }

        if (!Modifier.isStatic(handler.getModifiers())) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `" + named + "` is not static.");
        }

        // The invocation reads the declaring class, and a route provider is under no rule to be
        // public.
        if (!handler.canAccess(null)) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `" + named + "` is not accessible.");
        }

        return validateHandler(handler);
    }

    /**
     * Get the handler, held to the parameters it takes and the response it returns.
     *
     * @param handler the handler method
     * @return the handler method
     */
    protected Method validateHandler(Method handler) {
        // A route that names no handler runs the annotated method, and a cached route answers a
        // call alike, so the collection rejects a signature that cannot answer one.
        String named = handler.getDeclaringClass().getName() + "#" + handler.getName();

        // Wrong parameters reach the invocation as an IllegalArgumentException, which names
        // neither the class nor the method.
        if (!Arrays.equals(handler.getParameterTypes(), HANDLER_PARAMETERS)) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `"
                            + named
                            + "` does not take a ContainerContract and a RouteContract.");
        }

        // An unchecked return type reaches the call as a ClassCastException, and a void one as
        // a null response that every stage past the dispatch reads as non-null.
        if (!ServiceResponseContract.class.isAssignableFrom(handler.getReturnType())) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `"
                            + named
                            + "` returns "
                            + handler.getReturnType().getName()
                            + " rather than a ServiceResponseContract.");
        }

        return handler;
    }

    /**
     * Get the handler for an annotated controller method.
     *
     * @param clazz the controller class
     * @param method the annotated method
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract>
            controllerHandler(Class<?> clazz, Method method) {
        Method handler = validateHandler(method);

        // The controller comes from the container rather than from a no-argument constructor,
        // which a controller with dependencies does not have.
        return (container, route) -> invoke(handler, container.get(clazz), container, route);
    }

    /**
     * Invoke a handler method with the container and the route.
     *
     * @param handler the handler method
     * @param instance the instance to invoke it on, or null for a static handler
     * @param container the container
     * @param route the matched route
     * @return the handler's response
     */
    protected ServiceResponseContract invoke(
            Method handler,
            @Nullable Object instance,
            ContainerContract container,
            RouteContract route) {
        try {
            return (ServiceResponseContract) handler.invoke(instance, container, route);
        } catch (InvocationTargetException e) {
            // Surface the handler's own throwable (e.g. a framework-thrown CancelledException)
            // rather than the reflection wrapper, so ServiceHandler can map it to the correct
            // status instead of a blanket INTERNAL.
            // getCause() is the handler's own throwable — always present on an
            // InvocationTargetException — so there is no null case to defend against.
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new RuntimeException(cause);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected RouteContract applyMiddleware(RouteContract route, Method method) {
        Middleware[] middlewares = method.getAnnotationsByType(Middleware.class);

        for (Middleware middleware : middlewares) {
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

            if (ResponseSentMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route =
                        route.withAddedResponseSentMiddleware(
                                List.of(
                                        (Class<? extends ResponseSentMiddlewareContract>)
                                                middlewareClass));
            }
        }

        return route;
    }
}
