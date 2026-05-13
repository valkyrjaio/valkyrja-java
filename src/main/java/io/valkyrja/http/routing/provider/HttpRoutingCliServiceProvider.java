/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.provider;

import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;

import java.util.Map;
import java.util.function.Consumer;

public class HttpRoutingCliServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ListCommand.class, HttpRoutingCliServiceProvider::publishListCommand);
    }

    public static void publishListCommand(ContainerContract container) {
        container.setSingleton(
                ListCommand.class,
                new ListCommand(
                        container.getSingleton(RouteCollectionContract.class),
                        container.getSingleton(OutputFactoryContract.class)));
    }
}