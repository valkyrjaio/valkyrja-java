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
