/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import java.util.List;

/** Fixture route provider supplying the {@link GreeterControllerFixture} to the service map. */
public final class GreeterRouteProviderFixture implements GrpcRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of(GreeterControllerFixture.class);
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
