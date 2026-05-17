/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoRequestStructException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoResponseStructException;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

public class Route implements RouteContract {

    protected String path;
    protected String name;
    protected BiFunction<ContainerContract, RouteContract, ResponseContract> handler;
    protected List<RequestMethod> requestMethods;
    protected List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware;
    protected List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware;
    protected List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware;
    protected List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware;
    protected List<Class<? extends TerminatedMiddlewareContract>> terminatedMiddleware;
    protected @Nullable RequestStructContract requestStruct;
    protected @Nullable ResponseStructContract responseStruct;

    public Route(
            String path,
            String name,
            BiFunction<ContainerContract, RouteContract, ResponseContract> handler,
            List<RequestMethod> requestMethods,
            List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
            List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
            List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
            List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware,
            List<Class<? extends TerminatedMiddlewareContract>> terminatedMiddleware,
            @Nullable RequestStructContract requestStruct,
            @Nullable ResponseStructContract responseStruct) {
        this.path = path;
        this.name = name;
        this.handler = handler;
        this.requestMethods = new ArrayList<>(requestMethods);
        this.routeMatchedMiddleware = new ArrayList<>(routeMatchedMiddleware);
        this.routeDispatchedMiddleware = new ArrayList<>(routeDispatchedMiddleware);
        this.throwableCaughtMiddleware = new ArrayList<>(throwableCaughtMiddleware);
        this.sendingResponseMiddleware = new ArrayList<>(sendingResponseMiddleware);
        this.terminatedMiddleware = new ArrayList<>(terminatedMiddleware);
        this.requestStruct = requestStruct;
        this.responseStruct = responseStruct;
    }

    public Route(
            String path,
            String name,
            BiFunction<ContainerContract, RouteContract, ResponseContract> handler) {
        this(
                path,
                name,
                handler,
                List.of(RequestMethod.HEAD, RequestMethod.GET),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public RouteContract withPath(String path) {
        Route copy = copy();
        copy.path = getFilteredPath(path);
        return copy;
    }

    @Override
    public RouteContract withAddedPath(String path) {
        Route copy = copy();
        copy.path = getFilteredPath(getFilteredPath(this.path) + getFilteredPath(path));
        return copy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RouteContract withName(String name) {
        Route copy = copy();
        copy.name = name;
        return copy;
    }

    @Override
    public RouteContract withAddedName(String name) {
        Route copy = copy();
        copy.name = this.name + name;
        return copy;
    }

    @Override
    public BiFunction<ContainerContract, RouteContract, ResponseContract> getHandler() {
        return handler;
    }

    @Override
    public RouteContract withHandler(
            BiFunction<ContainerContract, RouteContract, ResponseContract> handler) {
        Route copy = copy();
        copy.handler = handler;
        return copy;
    }

    @Override
    public List<RequestMethod> getRequestMethods() {
        return List.copyOf(requestMethods);
    }

    @Override
    public boolean hasRequestMethod(RequestMethod requestMethod) {
        return requestMethods.contains(requestMethod);
    }

    @Override
    public RouteContract withRequestMethods(RequestMethod... requestMethods) {
        Route copy = copy();
        copy.requestMethods = new ArrayList<>(Arrays.asList(requestMethods));
        return copy;
    }

    @Override
    public RouteContract withAddedRequestMethods(RequestMethod... requestMethods) {
        Route copy = copy();
        for (RequestMethod method : requestMethods) {
            if (!copy.requestMethods.contains(method)) {
                copy.requestMethods.add(method);
            }
        }
        return copy;
    }

    @Override
    public List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware() {
        return List.copyOf(routeMatchedMiddleware);
    }

    @Override
    @SafeVarargs
    public final RouteContract withRouteMatchedMiddleware(
            Class<? extends RouteMatchedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.routeMatchedMiddleware = new ArrayList<>(Arrays.asList(middleware));
        return copy;
    }

    @Override
    @SafeVarargs
    public final RouteContract withAddedRouteMatchedMiddleware(
            Class<? extends RouteMatchedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.routeMatchedMiddleware.addAll(Arrays.asList(middleware));
        return copy;
    }

    @Override
    public List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware() {
        return List.copyOf(routeDispatchedMiddleware);
    }

    @Override
    @SafeVarargs
    public final RouteContract withRouteDispatchedMiddleware(
            Class<? extends RouteDispatchedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.routeDispatchedMiddleware = new ArrayList<>(Arrays.asList(middleware));
        return copy;
    }

    @Override
    @SafeVarargs
    public final RouteContract withAddedRouteDispatchedMiddleware(
            Class<? extends RouteDispatchedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.routeDispatchedMiddleware.addAll(Arrays.asList(middleware));
        return copy;
    }

    @Override
    public List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware() {
        return List.copyOf(throwableCaughtMiddleware);
    }

    @Override
    @SafeVarargs
    public final RouteContract withThrowableCaughtMiddleware(
            Class<? extends ThrowableCaughtMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.throwableCaughtMiddleware = new ArrayList<>(Arrays.asList(middleware));
        return copy;
    }

    @Override
    @SafeVarargs
    public final RouteContract withAddedThrowableCaughtMiddleware(
            Class<? extends ThrowableCaughtMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.throwableCaughtMiddleware.addAll(Arrays.asList(middleware));
        return copy;
    }

    @Override
    public List<Class<? extends SendingResponseMiddlewareContract>> getSendingResponseMiddleware() {
        return List.copyOf(sendingResponseMiddleware);
    }

    @Override
    @SafeVarargs
    public final RouteContract withSendingResponseMiddleware(
            Class<? extends SendingResponseMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.sendingResponseMiddleware = new ArrayList<>(Arrays.asList(middleware));
        return copy;
    }

    @Override
    @SafeVarargs
    public final RouteContract withAddedSendingResponseMiddleware(
            Class<? extends SendingResponseMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.sendingResponseMiddleware.addAll(Arrays.asList(middleware));
        return copy;
    }

    @Override
    public List<Class<? extends TerminatedMiddlewareContract>> getTerminatedMiddleware() {
        return List.copyOf(terminatedMiddleware);
    }

    @Override
    @SafeVarargs
    public final RouteContract withTerminatedMiddleware(
            Class<? extends TerminatedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.terminatedMiddleware = new ArrayList<>(Arrays.asList(middleware));
        return copy;
    }

    @Override
    @SafeVarargs
    public final RouteContract withAddedTerminatedMiddleware(
            Class<? extends TerminatedMiddlewareContract>... middleware) {
        Route copy = copy();
        copy.terminatedMiddleware.addAll(Arrays.asList(middleware));
        return copy;
    }

    @Override
    public boolean hasRequestStruct() {
        return requestStruct != null;
    }

    @Override
    public RequestStructContract getRequestStruct() {
        if (requestStruct == null) {
            throw new HttpRoutingNoRequestStructException(
                    "No request struct was set for this route");
        }
        return requestStruct;
    }

    @Override
    public RouteContract withRequestStruct(RequestStructContract requestStruct) {
        Route copy = copy();
        copy.requestStruct = requestStruct;
        return copy;
    }

    @Override
    public boolean hasResponseStruct() {
        return responseStruct != null;
    }

    @Override
    public ResponseStructContract getResponseStruct() {
        if (responseStruct == null) {
            throw new HttpRoutingNoResponseStructException(
                    "No response struct was set for this route");
        }
        return responseStruct;
    }

    @Override
    public RouteContract withResponseStruct(ResponseStructContract responseStruct) {
        Route copy = copy();
        copy.responseStruct = responseStruct;
        return copy;
    }

    protected Route copy() {
        return new Route(
                path,
                name,
                handler,
                requestMethods,
                routeMatchedMiddleware,
                routeDispatchedMiddleware,
                throwableCaughtMiddleware,
                sendingResponseMiddleware,
                terminatedMiddleware,
                requestStruct,
                responseStruct);
    }

    protected String getFilteredPath(String path) {
        String trimmed = path.replaceAll("^/+|/+$", "");
        return trimmed.isEmpty() ? "/" : "/" + trimmed;
    }
}
