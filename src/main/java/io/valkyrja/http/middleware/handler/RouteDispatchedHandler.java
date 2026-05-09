/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.handler;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

public class RouteDispatchedHandler extends Handler<RouteDispatchedMiddlewareContract> implements RouteDispatchedHandlerContract {

    public RouteDispatchedHandler(String... middleware) {
        super(middleware);
    }

    @Override
    public ResponseContract routeDispatched(ServerRequestContract request, ResponseContract response, RouteContract route) {
        String next = this.next;
        return next != null
                ? getMiddleware(next).routeDispatched(request, response, route, this)
                : response;
    }
}