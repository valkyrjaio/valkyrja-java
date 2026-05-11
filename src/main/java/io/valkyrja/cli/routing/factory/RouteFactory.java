/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.factory;

import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;

public abstract class RouteFactory {

    public static Route fromRoute(RouteContract route) {
        return new Route(
            route.getName(),
            route.getDescription(),
            route.getHandler(),
            route.hasHelpText() ? route.getHelpText() : null,
            route.getRouteMatchedMiddleware(),
            route.getRouteDispatchedMiddleware(),
            route.getThrowableCaughtMiddleware(),
            route.getExitedMiddleware(),
            route.getArguments(),
            route.getOptions()
        );
    }
}
