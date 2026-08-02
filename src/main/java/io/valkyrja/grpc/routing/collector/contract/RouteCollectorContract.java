/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.collector.contract;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;

/** Builds {@code Route}s from annotated gRPC service controller classes. */
public interface RouteCollectorContract {

    List<RouteContract> getRoutes(Class<?>... controllerClasses);
}
