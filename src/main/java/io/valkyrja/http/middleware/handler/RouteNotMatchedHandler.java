/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.handler;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;

public class RouteNotMatchedHandler extends Handler<RouteNotMatchedMiddlewareContract>
        implements RouteNotMatchedHandlerContract {

    @SafeVarargs
    public RouteNotMatchedHandler(
            ContainerContract container,
            Class<? extends RouteNotMatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ResponseContract routeNotMatched(
            ServerRequestContract request, ResponseContract response) {
        Class<? extends RouteNotMatchedMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).routeNotMatched(request, response, this)
                : response;
    }
}
