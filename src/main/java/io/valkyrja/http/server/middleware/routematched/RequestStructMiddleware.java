/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.middleware.routematched;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;

public class RequestStructMiddleware implements RouteMatchedMiddlewareContract {

    @Override
    public RouteMatchedResult routeMatched(ServerRequestContract request, RouteContract route, RouteMatchedHandlerContract handler) {
        if (route.hasRequestStruct()) {
            RequestStructContract struct = route.getRequestStruct();
            ResponseContract response = ensureRequestConformsToMessage(request, route, struct);
            if (response != null) {
                return new RouteMatchedResult(route, response);
            }
        }

        return handler.routeMatched(request, route);
    }

    protected ResponseContract ensureRequestConformsToMessage(ServerRequestContract request, RouteContract route, RequestStructContract struct) {
        ResponseContract extra = ensureRequestHasNoExtraData(request, route, struct);
        if (extra != null) return extra;
        return ensureRequestIsValid(request, route, struct);
    }

    protected ResponseContract ensureRequestHasNoExtraData(ServerRequestContract request, RouteContract route, RequestStructContract struct) {
        if (struct.determineIfRequestContainsExtraData(request)) {
            return getExtraDataErrorResponse(request, route, struct);
        }
        return null;
    }

    protected ResponseContract getExtraDataErrorResponse(ServerRequestContract request, RouteContract route, RequestStructContract struct) {
        return new Response(new io.valkyrja.http.message.stream.Stream(), StatusCode.PAYLOAD_TOO_LARGE, new HeaderCollection());
    }

    protected ResponseContract ensureRequestIsValid(ServerRequestContract request, RouteContract route, RequestStructContract struct) {
        if (!struct.validate(request).validateRules()) {
            return getValidationErrorsResponse(request, route, struct);
        }
        return null;
    }

    protected ResponseContract getValidationErrorsResponse(ServerRequestContract request, RouteContract route, RequestStructContract struct) {
        return new Response(new io.valkyrja.http.message.stream.Stream(), StatusCode.BAD_REQUEST, new HeaderCollection());
    }
}