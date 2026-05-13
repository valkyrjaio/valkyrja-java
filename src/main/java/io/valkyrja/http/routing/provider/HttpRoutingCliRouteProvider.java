/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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