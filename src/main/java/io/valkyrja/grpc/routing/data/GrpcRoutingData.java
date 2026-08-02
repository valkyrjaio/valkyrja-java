/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.data;

import io.valkyrja.grpc.routing.data.contract.GrpcRoutingDataContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Default {@link GrpcRoutingDataContract} implementation. The generated {@code AppGrpcRoutingData}
 * record supplies the real service map; this empty default is used when no cache is generated.
 */
public record GrpcRoutingData(Map<String, Supplier<RouteContract>> routes)
        implements GrpcRoutingDataContract {

    public GrpcRoutingData {
        routes = Map.copyOf(routes);
    }

    public GrpcRoutingData() {
        this(Map.of());
    }
}
