/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** Middleware run after a route is matched, before the user handler. */
public interface RouteMatchedMiddlewareContract {

    RouteMatchedResult routeMatched(
            ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler);
}
