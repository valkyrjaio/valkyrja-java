/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.data;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

/** Immutable {@link RouteContract} implementation. */
public class Route implements RouteContract {

    protected String method;
    protected String service;
    protected String methodName;
    protected BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler;
    protected @Nullable Class<?> requestType;
    protected @Nullable Class<?> responseType;
    protected boolean clientStreaming;
    protected boolean serverStreaming;
    protected List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware;
    protected List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware;
    protected List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware;
    protected List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware;
    protected List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware;

    public Route(
            String method,
            BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler) {
        this(
                method,
                serviceOf(method),
                methodNameOf(method),
                handler,
                null,
                null,
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public Route(
            String method,
            String service,
            String methodName,
            BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler,
            @Nullable Class<?> requestType,
            @Nullable Class<?> responseType,
            boolean clientStreaming,
            boolean serverStreaming,
            List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
            List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
            List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
            List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware,
            List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware) {
        this.method = method;
        this.service = service;
        this.methodName = methodName;
        this.handler = handler;
        this.requestType = requestType;
        this.responseType = responseType;
        this.clientStreaming = clientStreaming;
        this.serverStreaming = serverStreaming;
        // Copied immutably so the getters can hand the lists out directly on the dispatch hot
        // path without a per-read defensive copy, and callers cannot mutate a built route.
        this.routeMatchedMiddleware = List.copyOf(routeMatchedMiddleware);
        this.routeDispatchedMiddleware = List.copyOf(routeDispatchedMiddleware);
        this.throwableCaughtMiddleware = List.copyOf(throwableCaughtMiddleware);
        this.sendingResponseMiddleware = List.copyOf(sendingResponseMiddleware);
        this.responseSentMiddleware = List.copyOf(responseSentMiddleware);
    }

    protected Route copy() {
        return new Route(
                method,
                service,
                methodName,
                handler,
                requestType,
                responseType,
                clientStreaming,
                serverStreaming,
                routeMatchedMiddleware,
                routeDispatchedMiddleware,
                throwableCaughtMiddleware,
                sendingResponseMiddleware,
                responseSentMiddleware);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public BiFunction<ContainerContract, RouteContract, ServiceResponseContract> getHandler() {
        return handler;
    }

    @Override
    public RouteContract withHandler(
            BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler) {
        Route copy = copy();
        copy.handler = handler;
        return copy;
    }

    @Override
    public @Nullable Class<?> getRequestType() {
        return requestType;
    }

    @Override
    public RouteContract withRequestType(@Nullable Class<?> requestType) {
        Route copy = copy();
        copy.requestType = requestType;
        return copy;
    }

    @Override
    public @Nullable Class<?> getResponseType() {
        return responseType;
    }

    @Override
    public RouteContract withResponseType(@Nullable Class<?> responseType) {
        Route copy = copy();
        copy.responseType = responseType;
        return copy;
    }

    @Override
    public boolean isClientStreaming() {
        return clientStreaming;
    }

    @Override
    public RouteContract withClientStreaming(boolean clientStreaming) {
        Route copy = copy();
        copy.clientStreaming = clientStreaming;
        return copy;
    }

    @Override
    public boolean isServerStreaming() {
        return serverStreaming;
    }

    @Override
    public RouteContract withServerStreaming(boolean serverStreaming) {
        Route copy = copy();
        copy.serverStreaming = serverStreaming;
        return copy;
    }

    @Override
    public List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware() {
        return routeMatchedMiddleware;
    }

    @Override
    public RouteContract withRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeMatchedMiddleware = List.copyOf(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        List<Class<? extends RouteMatchedMiddlewareContract>> merged =
                new ArrayList<>(this.routeMatchedMiddleware);
        merged.addAll(middleware);
        copy.routeMatchedMiddleware = List.copyOf(merged);
        return copy;
    }

    @Override
    public List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware() {
        return routeDispatchedMiddleware;
    }

    @Override
    public RouteContract withRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeDispatchedMiddleware = List.copyOf(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        List<Class<? extends RouteDispatchedMiddlewareContract>> merged =
                new ArrayList<>(this.routeDispatchedMiddleware);
        merged.addAll(middleware);
        copy.routeDispatchedMiddleware = List.copyOf(merged);
        return copy;
    }

    @Override
    public List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware() {
        return throwableCaughtMiddleware;
    }

    @Override
    public RouteContract withThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.throwableCaughtMiddleware = List.copyOf(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware) {
        Route copy = copy();
        List<Class<? extends ThrowableCaughtMiddlewareContract>> merged =
                new ArrayList<>(this.throwableCaughtMiddleware);
        merged.addAll(middleware);
        copy.throwableCaughtMiddleware = List.copyOf(merged);
        return copy;
    }

    @Override
    public List<Class<? extends SendingResponseMiddlewareContract>> getSendingResponseMiddleware() {
        return sendingResponseMiddleware;
    }

    @Override
    public RouteContract withSendingResponseMiddleware(
            List<Class<? extends SendingResponseMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.sendingResponseMiddleware = List.copyOf(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedSendingResponseMiddleware(
            List<Class<? extends SendingResponseMiddlewareContract>> middleware) {
        Route copy = copy();
        List<Class<? extends SendingResponseMiddlewareContract>> merged =
                new ArrayList<>(this.sendingResponseMiddleware);
        merged.addAll(middleware);
        copy.sendingResponseMiddleware = List.copyOf(merged);
        return copy;
    }

    @Override
    public List<Class<? extends ResponseSentMiddlewareContract>> getResponseSentMiddleware() {
        return responseSentMiddleware;
    }

    @Override
    public RouteContract withResponseSentMiddleware(
            List<Class<? extends ResponseSentMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.responseSentMiddleware = List.copyOf(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedResponseSentMiddleware(
            List<Class<? extends ResponseSentMiddlewareContract>> middleware) {
        Route copy = copy();
        List<Class<? extends ResponseSentMiddlewareContract>> merged =
                new ArrayList<>(this.responseSentMiddleware);
        merged.addAll(middleware);
        copy.responseSentMiddleware = List.copyOf(merged);
        return copy;
    }

    /**
     * Extract the {@code "package.Service"} portion of a {@code "/package.Service/Method"} method.
     *
     * @param method the fully-qualified method
     * @return the service name
     */
    protected static String serviceOf(String method) {
        int firstSlash = method.indexOf('/');
        int lastSlash = method.lastIndexOf('/');

        if (firstSlash != 0 || lastSlash <= firstSlash) {
            throw new GrpcRoutingInvalidMethodException(
                    "Invalid gRPC method `" + method + "`; expected `/package.Service/Method`");
        }

        return method.substring(firstSlash + 1, lastSlash);
    }

    /**
     * Extract the {@code "Method"} portion of a {@code "/package.Service/Method"} method.
     *
     * @param method the fully-qualified method
     * @return the bare method name
     */
    protected static String methodNameOf(String method) {
        int lastSlash = method.lastIndexOf('/');

        if (lastSlash == method.length() - 1) {
            throw new GrpcRoutingInvalidMethodException(
                    "Invalid gRPC method `" + method + "`; expected `/package.Service/Method`");
        }

        return method.substring(lastSlash + 1);
    }
}
