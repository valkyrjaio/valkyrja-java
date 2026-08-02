/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.data.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

/**
 * The immutable value stored in the service map, analogous to HTTP's {@code Route} and CLI's {@code
 * Command}. Held in a {@code Map<String, Route>} keyed by fully-qualified method name.
 */
public interface RouteContract {

    /**
     * Get the fully-qualified method, {@code "/package.Service/Method"} — the map key.
     *
     * @return the method
     */
    String getMethod();

    /**
     * Get the service name, {@code "package.Service"}.
     *
     * @return the service
     */
    String getService();

    /**
     * Get the bare method name, {@code "Method"}.
     *
     * @return the method name
     */
    String getMethodName();

    /**
     * Get the handler that produces a {@link ServiceResponseContract} for a matched call.
     *
     * @return the handler
     */
    BiFunction<ContainerContract, RouteContract, ServiceResponseContract> getHandler();

    /**
     * Return a copy with the given handler.
     *
     * @param handler the new handler
     * @return a new route
     */
    RouteContract withHandler(
            BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler);

    /**
     * Get the generated protobuf request message type, or null if unspecified.
     *
     * @return the request type
     */
    @Nullable Class<?> getRequestType();

    /**
     * Return a copy with the given request type.
     *
     * @param requestType the request type
     * @return a new route
     */
    RouteContract withRequestType(@Nullable Class<?> requestType);

    /**
     * Get the generated protobuf response message type, or null if unspecified.
     *
     * @return the response type
     */
    @Nullable Class<?> getResponseType();

    /**
     * Return a copy with the given response type.
     *
     * @param responseType the response type
     * @return a new route
     */
    RouteContract withResponseType(@Nullable Class<?> responseType);

    /**
     * Whether the client streams multiple request messages.
     *
     * @return true if client-streaming
     */
    boolean isClientStreaming();

    /**
     * Return a copy with the given client-streaming flag.
     *
     * @param clientStreaming the flag
     * @return a new route
     */
    RouteContract withClientStreaming(boolean clientStreaming);

    /**
     * Whether the server streams multiple response messages.
     *
     * @return true if server-streaming
     */
    boolean isServerStreaming();

    /**
     * Return a copy with the given server-streaming flag.
     *
     * @param serverStreaming the flag
     * @return a new route
     */
    RouteContract withServerStreaming(boolean serverStreaming);

    List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware();

    RouteContract withRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware);

    RouteContract withAddedRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware);

    List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware();

    RouteContract withRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware);

    RouteContract withAddedRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware);

    List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware();

    RouteContract withThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware);

    RouteContract withAddedThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware);

    List<Class<? extends SendingResponseMiddlewareContract>> getSendingResponseMiddleware();

    RouteContract withSendingResponseMiddleware(
            List<Class<? extends SendingResponseMiddlewareContract>> middleware);

    RouteContract withAddedSendingResponseMiddleware(
            List<Class<? extends SendingResponseMiddlewareContract>> middleware);

    List<Class<? extends ResponseSentMiddlewareContract>> getResponseSentMiddleware();

    RouteContract withResponseSentMiddleware(
            List<Class<? extends ResponseSentMiddlewareContract>> middleware);

    RouteContract withAddedResponseSentMiddleware(
            List<Class<? extends ResponseSentMiddlewareContract>> middleware);
}
