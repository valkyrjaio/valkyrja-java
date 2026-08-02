/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.provider;

import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.routing.cli.command.ListCommand;
import java.util.List;

public class HttpRoutingCliRouteProvider implements CliRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of(ListCommand.class);
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }

    public static OutputContract listHandler(ContainerContract container, RouteContract route) {
        return container.getSingleton(ListCommand.class).run();
    }
}
