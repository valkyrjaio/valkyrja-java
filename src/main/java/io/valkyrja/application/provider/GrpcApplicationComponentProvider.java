/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.grpc.middleware.provider.GrpcMiddlewareComponentProvider;
import io.valkyrja.grpc.routing.provider.GrpcRoutingComponentProvider;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.grpc.server.provider.GrpcServerComponentProvider;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

public class GrpcApplicationComponentProvider implements ComponentProviderContract {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(
                new ApplicationComponentProvider(),
                new GrpcMiddlewareComponentProvider(),
                new GrpcRoutingComponentProvider(),
                new GrpcServerComponentProvider());
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

    @Override
    public List<GrpcRouteProviderContract> getGrpcProviders(ApplicationContract app) {
        return List.of();
    }
}
