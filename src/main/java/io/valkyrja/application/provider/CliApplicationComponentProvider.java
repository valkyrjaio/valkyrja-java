/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.abstract_.ComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.interaction.provider.CliInteractionComponentProvider;
import io.valkyrja.cli.middleware.provider.CliMiddlewareComponentProvider;
import io.valkyrja.cli.routing.provider.CliRoutingComponentProvider;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.cli.server.provider.CliServerComponentProvider;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import io.valkyrja.log.provider.LogComponentProvider;
import java.util.List;

public class CliApplicationComponentProvider extends ComponentProvider {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(
                new ApplicationComponentProvider(),
                new CliInteractionComponentProvider(),
                new CliMiddlewareComponentProvider(),
                new CliRoutingComponentProvider(),
                new CliServerComponentProvider(),
                new LogComponentProvider());
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<ListenerProviderContract> getEventProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<CliRouteProviderContract> getCliProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<HttpRouteProviderContract> getHttpProviders(ApplicationContract app) {
        return List.of();
    }
}
