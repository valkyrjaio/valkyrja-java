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

    List<String> getRouteMatchedMiddleware();

    RouteContract withRouteMatchedMiddleware(String... middleware);

    RouteContract withAddedRouteMatchedMiddleware(String... middleware);

    List<String> getRouteDispatchedMiddleware();

    RouteContract withRouteDispatchedMiddleware(String... middleware);

    RouteContract withAddedRouteDispatchedMiddleware(String... middleware);

    List<String> getThrowableCaughtMiddleware();

    RouteContract withThrowableCaughtMiddleware(String... middleware);

    RouteContract withAddedThrowableCaughtMiddleware(String... middleware);

    List<String> getSendingResponseMiddleware();

    RouteContract withSendingResponseMiddleware(String... middleware);

    RouteContract withAddedSendingResponseMiddleware(String... middleware);

    List<String> getTerminatedMiddleware();

    RouteContract withTerminatedMiddleware(String... middleware);

    RouteContract withAddedTerminatedMiddleware(String... middleware);

    boolean hasRequestStruct();

    RequestStructContract getRequestStruct();

    RouteContract withRequestStruct(RequestStructContract requestStruct);

    boolean hasResponseStruct();

    ResponseStructContract getResponseStruct();

    RouteContract withResponseStruct(ResponseStructContract responseStruct);
}