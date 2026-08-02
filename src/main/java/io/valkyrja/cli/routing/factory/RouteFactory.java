/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
                route.getProcessExitingMiddleware(),
                route.getArguments(),
                route.getOptions());
    }
}
