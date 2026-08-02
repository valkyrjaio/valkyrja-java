/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.handler;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class RouteDispatchedHandler extends Handler<RouteDispatchedMiddlewareContract>
        implements RouteDispatchedHandlerContract {

    @SafeVarargs
    public RouteDispatchedHandler(
            ContainerContract container,
            Class<? extends RouteDispatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public OutputContract routeDispatched(
            InputContract input, OutputContract output, RouteContract route) {
        Class<? extends RouteDispatchedMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).routeDispatched(input, output, route, this)
                : output;
    }
}
