/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.middleware.routematched;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;

public class ResponseStructMiddleware implements RouteDispatchedMiddlewareContract {

    @Override
    public ResponseContract routeDispatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteContract route,
            RouteDispatchedHandlerContract handler) {
        if (response instanceof JsonResponseContract jsonResponse && route.hasResponseStruct()) {
            ResponseStructContract responseStruct = route.getResponseStruct();
            response = updateJsonWithResponseStruct(jsonResponse, responseStruct);
        }

        return handler.routeDispatched(request, response, route);
    }

    protected JsonResponseContract updateJsonWithResponseStruct(
            JsonResponseContract response, ResponseStructContract responseStruct) {
        return response.withJsonAsBody(
                responseStruct.getStructuredData(response.getBodyAsJson(), true));
    }
}
