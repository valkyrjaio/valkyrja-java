/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
    public ResponseContract routeDispatched(ServerRequestContract request, ResponseContract response, RouteContract route, RouteDispatchedHandlerContract handler) {
        if (response instanceof JsonResponseContract jsonResponse && route.hasResponseStruct()) {
            ResponseStructContract responseStruct = route.getResponseStruct();
            response = updateJsonWithResponseStruct(jsonResponse, responseStruct);
        }

        return handler.routeDispatched(request, response, route);
    }

    protected JsonResponseContract updateJsonWithResponseStruct(JsonResponseContract response, ResponseStructContract responseStruct) {
        return response.withJsonAsBody(responseStruct.getStructuredData(response.getBodyAsJson(), true));
    }
}