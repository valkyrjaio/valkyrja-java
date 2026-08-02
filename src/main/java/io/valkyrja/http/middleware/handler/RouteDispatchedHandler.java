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
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

public class RouteDispatchedHandler extends Handler<RouteDispatchedMiddlewareContract>
        implements RouteDispatchedHandlerContract {

    @SafeVarargs
    public RouteDispatchedHandler(
            ContainerContract container,
            Class<? extends RouteDispatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ResponseContract routeDispatched(
            ServerRequestContract request, ResponseContract response, RouteContract route) {
        Class<? extends RouteDispatchedMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).routeDispatched(request, response, route, this)
                : response;
    }
}
