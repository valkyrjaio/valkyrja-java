/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.application.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import io.valkyrja.tests.fixtures.event.provider.ListenerProviderFixture;
import java.util.List;

/** Test component provider supplying a single event listener provider. */
public final class EventComponentProviderFixture implements ComponentProviderContract {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<ListenerProviderContract> getEventProviders(ApplicationContract app) {
        return List.of(new ListenerProviderFixture());
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
