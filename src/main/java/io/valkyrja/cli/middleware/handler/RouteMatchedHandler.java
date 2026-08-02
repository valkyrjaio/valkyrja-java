/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.handler;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class RouteMatchedHandler extends Handler<RouteMatchedMiddlewareContract>
        implements RouteMatchedHandlerContract {

    @SafeVarargs
    public RouteMatchedHandler(
            ContainerContract container,
            Class<? extends RouteMatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public Object routeMatched(InputContract input, RouteContract route) {
        Class<? extends RouteMatchedMiddlewareContract> next = this.next;
        return next != null ? getMiddleware(next).routeMatched(input, route, this) : route;
    }
}
