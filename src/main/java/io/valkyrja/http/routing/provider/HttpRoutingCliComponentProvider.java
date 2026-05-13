/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;

import java.util.List;

public class HttpRoutingCliComponentProvider implements ComponentProviderContract {

    @Override
    public List<Class<? extends ComponentProviderContract>> getComponentProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<Class<? extends ServiceProviderContract>> getContainerProviders(ApplicationContract app) {
        return List.of(HttpRoutingCliServiceProvider.class);
    }

    @Override
    public List<Class<? extends ListenerProviderContract>> getEventProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Class<? extends CliRouteProviderContract>> getCliProviders(ApplicationContract app) {
        return List.of(HttpRoutingCliRouteProvider.class);
    }

    @Override
    public List<Class<? extends HttpRouteProviderContract>> getHttpProviders(ApplicationContract app) {
        return List.of();
    }
}