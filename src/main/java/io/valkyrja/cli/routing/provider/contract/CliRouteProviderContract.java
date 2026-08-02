/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.provider.contract;

import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.List;

public interface CliRouteProviderContract {

    List<Class<?>> getControllerClasses();

    List<RouteContract> getRoutes();
}
