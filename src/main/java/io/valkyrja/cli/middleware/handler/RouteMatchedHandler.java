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
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class RouteMatchedHandler extends Handler implements RouteMatchedHandlerContract {

    public RouteMatchedHandler(ContainerContract container) {
        super(container);
    }

    @Override
    public Object routeMatched(InputContract input, RouteContract route) {
        Class<?> next = this.next;
        return next != null
                ? ((RouteMatchedMiddlewareContract) getMiddleware(next)).routeMatched(input, route, this)
                : route;
    }
}
