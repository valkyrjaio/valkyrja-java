/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;

import java.util.List;
import java.util.function.BiFunction;

public interface RouteContract {

    String getPath();

    RouteContract withPath(String path);

    RouteContract withAddedPath(String path);

    String getName();

    RouteContract withName(String name);

    RouteContract withAddedName(String name);

    BiFunction<ContainerContract, RouteContract, ResponseContract> getHandler();

    RouteContract withHandler(BiFunction<ContainerContract, RouteContract, ResponseContract> handler);

    List<RequestMethod> getRequestMethods();

    boolean hasRequestMethod(RequestMethod requestMethod);

    RouteContract withRequestMethods(RequestMethod... requestMethods);

    RouteContract withAddedRequestMethods(RequestMethod... requestMethods);

    List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware();

    RouteContract withRouteMatchedMiddleware(Class<? extends RouteMatchedMiddlewareContract>... middleware);

    RouteContract withAddedRouteMatchedMiddleware(Class<? extends RouteMatchedMiddlewareContract>... middleware);

    List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware();

    RouteContract withRouteDispatchedMiddleware(Class<? extends RouteDispatchedMiddlewareContract>... middleware);

    RouteContract withAddedRouteDispatchedMiddleware(Class<? extends RouteDispatchedMiddlewareContract>... middleware);

    List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware();

    RouteContract withThrowableCaughtMiddleware(Class<? extends ThrowableCaughtMiddlewareContract>... middleware);

    RouteContract withAddedThrowableCaughtMiddleware(Class<? extends ThrowableCaughtMiddlewareContract>... middleware);

    List<Class<? extends SendingResponseMiddlewareContract>> getSendingResponseMiddleware();

    RouteContract withSendingResponseMiddleware(Class<? extends SendingResponseMiddlewareContract>... middleware);

    RouteContract withAddedSendingResponseMiddleware(Class<? extends SendingResponseMiddlewareContract>... middleware);

    List<Class<? extends TerminatedMiddlewareContract>> getTerminatedMiddleware();

    RouteContract withTerminatedMiddleware(Class<? extends TerminatedMiddlewareContract>... middleware);

    RouteContract withAddedTerminatedMiddleware(Class<? extends TerminatedMiddlewareContract>... middleware);

    boolean hasRequestStruct();

    RequestStructContract getRequestStruct();

    RouteContract withRequestStruct(RequestStructContract requestStruct);

    boolean hasResponseStruct();

    ResponseStructContract getResponseStruct();

    RouteContract withResponseStruct(ResponseStructContract responseStruct);
}
