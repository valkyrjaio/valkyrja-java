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
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.message.provider.HttpMessageComponentProvider;
import io.valkyrja.http.middleware.provider.HttpMiddlewareComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingCliComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingComponentProvider;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import io.valkyrja.http.server.provider.HttpServerComponentProvider;

import java.util.List;

public class HttpApplicationComponentProvider implements ComponentProviderContract {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(
                new ApplicationComponentProvider(),
                new HttpMessageComponentProvider(),
                new HttpMiddlewareComponentProvider(),
                new HttpRoutingComponentProvider(),
                new HttpRoutingCliComponentProvider(),
                new HttpServerComponentProvider());
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
