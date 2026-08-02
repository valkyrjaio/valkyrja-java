/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.contract;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

public interface RouteDispatchedMiddlewareContract {

    ResponseContract routeDispatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteContract route,
            RouteDispatchedHandlerContract handler);
}
