/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.abstract_.ComponentProvider;
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

public class CliWithHttpApplicationComponentProvider extends ComponentProvider {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(
                new CliApplicationComponentProvider(),
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
