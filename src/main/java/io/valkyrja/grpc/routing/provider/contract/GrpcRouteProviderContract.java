/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.provider.contract;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;

/**
 * Supplies gRPC service controllers and/or pre-built routes for the service map, mirroring HTTP's
 * {@code HttpRouteProviderContract} and CLI's {@code CliRouteProviderContract}.
 */
public interface GrpcRouteProviderContract {

    List<Class<?>> getControllerClasses();

    List<RouteContract> getRoutes();
}
