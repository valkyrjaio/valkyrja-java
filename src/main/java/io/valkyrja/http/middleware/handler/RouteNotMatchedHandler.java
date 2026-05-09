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
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;

public class RouteNotMatchedHandler extends Handler<RouteNotMatchedMiddlewareContract> implements RouteNotMatchedHandlerContract {

    public RouteNotMatchedHandler(String... middleware) {
        super(middleware);
    }

    @Override
    public ResponseContract routeNotMatched(ServerRequestContract request, ResponseContract response) {
        String next = this.next;
        return next != null
                ? getMiddleware(next).routeNotMatched(request, response, this)
                : response;
    }
}