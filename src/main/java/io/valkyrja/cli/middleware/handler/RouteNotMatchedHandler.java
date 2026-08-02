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
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class RouteNotMatchedHandler extends Handler<RouteNotMatchedMiddlewareContract>
        implements RouteNotMatchedHandlerContract {

    @SafeVarargs
    public RouteNotMatchedHandler(
            ContainerContract container,
            Class<? extends RouteNotMatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public OutputContract routeNotMatched(InputContract input, OutputContract output) {
        Class<? extends RouteNotMatchedMiddlewareContract> next = this.next;
        return next != null ? getMiddleware(next).routeNotMatched(input, output, this) : output;
    }
}
