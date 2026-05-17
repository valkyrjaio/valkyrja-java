/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
