/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data.contract;

import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ProcessExitingMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;

public interface CliConfigContract extends ConfigContract {

    String applicationName();

    String defaultCommandName();

    List<Class<? extends InputReceivedMiddlewareContract>> inputReceivedMiddleware();

    List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware();

    List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware();

    List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware();

    List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware();

    List<Class<? extends ProcessExitingMiddlewareContract>> processExitingMiddleware();
}
