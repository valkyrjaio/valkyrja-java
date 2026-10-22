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
import io.valkyrja.grpc.routing.attribute.route.RouteHandler;
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

/**
 * Builds the service map by reflecting over {@link Service}-annotated controller classes: each
 * {@link Route} method becomes a {@code Route} keyed by {@code /service/name}, with its handler
 * resolved through the container and any {@link Middleware} dispatched to its stage.
 */
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
     * Resolve a message type element to the type it names, where {@code Void.class} means the
     * element was left at its default and the route holds no type.
     *
     * @param type the type the element names
     * @return the type, or null where the element names none
     */
    protected @Nullable Class<?> messageType(Class<?> type) {
        return type == Void.class ? null : type;
    }

    /**
     * Resolve the route's handler from the method-level {@link RouteHandler}, the {@link
     * Route#handler()} element, or the annotated method itself.
     *
     * @param routeAttribute the route attribute
     * @param clazz the controller class
     * @param method the annotated method
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> buildHandler(
            Route routeAttribute, Class<?> clazz, Method method) {
        RouteHandler annotation = method.getAnnotation(RouteHandler.class);

        // The convenience attribute is a second spelling of the one field, and a single-valued
        // field takes the convenience attribute's value, so it wins over the element. Either
        // spelling names no handler when it names the absent type.
        if (annotation != null && annotation.handlerClass() != Void.class) {
            return namedHandler(annotation.handlerClass(), annotation.handlerMethod());
        }

        RouteHandler element = routeAttribute.handler();

        if (element.handlerClass() != Void.class) {
            return namedHandler(element.handlerClass(), element.handlerMethod());
        }

        return controllerHandler(clazz, method);
    }

    /**
     * Build the handler a {@link RouteHandler} names — a static {@code (ContainerContract,
     * RouteContract)} method, typically on the route provider, which is how a controller with
     * constructor dependencies is reached: the handler resolves it from the container.
     *
     * @param handlerClass the class declaring the handler
     * @param handlerMethod the handler's method name
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> namedHandler(
            Class<?> handlerClass, String handlerMethod) {
        Method handler = resolveHandler(handlerClass, handlerMethod);

        return (container, route) -> invoke(handler, null, container, route);
    }

    /**
     * Resolve the method a {@link RouteHandler} names, and hold it to the signature a handler has.
     * Java cannot check the signature where the attribute names it, so the collector is the only
     * place left that can, and it runs once at bootstrap rather than once for each call.
     *
     * @param handlerClass the class declaring the handler
     * @param handlerMethod the handler's method name
     * @return the handler method
     */
    protected Method resolveHandler(Class<?> handlerClass, String handlerMethod) {
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

        return validateHandler(handler);
    }

    /**
     * Hold a handler to the parameters it takes and the response it returns, whichever spelling
     * named it. The cached route calls the handler directly, where a wrong signature is a compile
     * error, so the collected route rejects the same signature rather than reaching a call.
     *
     * @param handler the handler method
     * @return the handler method
     */
    protected Method validateHandler(Method handler) {
        String named = handler.getDeclaringClass().getName() + "#" + handler.getName();

        // Wrong parameters reach the invocation as an IllegalArgumentException, which names
        // neither the class nor the method.
        if (!Arrays.equals(handler.getParameterTypes(), HANDLER_PARAMETERS)) {
            throw new GrpcRoutingInvalidHandlerException(
                    "The route handler `"
                            + named
                            + "` does not take a ContainerContract and a RouteContract.");
        }

        // An unchecked return type reaches the router as a null response, which every stage past
        // the dispatch reads as non-null.
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
     * Build the handler for an annotated controller method. The controller is resolved from the
     * container rather than built with its no-argument constructor: the container knows how to
     * build it, and a controller with dependencies has no such constructor.
     *
     * @param clazz the controller class
     * @param method the annotated method
     * @return the handler
     */
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract>
            controllerHandler(Class<?> clazz, Method method) {
        Method handler = validateHandler(method);

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
