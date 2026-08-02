/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.provider;

import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import java.util.List;

public class CliRoutingCliRouteProvider implements CliRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of();
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
