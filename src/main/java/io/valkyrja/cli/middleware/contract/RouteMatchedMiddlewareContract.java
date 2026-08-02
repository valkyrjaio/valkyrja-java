/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;

public interface RouteMatchedMiddlewareContract {

    /** Returns either a RouteContract to dispatch, or OutputContract to short-circuit. */
    Object routeMatched(
            InputContract input, RouteContract route, RouteMatchedHandlerContract handler);
}
