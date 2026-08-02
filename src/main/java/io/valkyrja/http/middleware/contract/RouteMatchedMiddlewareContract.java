/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.contract;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

public interface RouteMatchedMiddlewareContract {

    RouteMatchedResult routeMatched(
            ServerRequestContract request,
            RouteContract route,
            RouteMatchedHandlerContract handler);
}
