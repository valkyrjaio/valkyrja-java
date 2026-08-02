/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.handler.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

public interface RouteMatchedHandlerContract
        extends HandlerContract<RouteMatchedMiddlewareContract> {

    RouteMatchedResult routeMatched(ServiceCallContract call, RouteContract route);
}
