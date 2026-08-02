/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.routing.data.GrpcRoutingData;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcRoutingData} record. */
final class GrpcRoutingDataTest {

    @Test
    void defaultHasNoRoutes() {
        assertTrue(new GrpcRoutingData().routes().isEmpty());
    }

    @Test
    void routesAccessorAndDefensiveCopy() {
        Map<String, Supplier<RouteContract>> routes = new HashMap<>();
        routes.put(
                "/pkg.Greeter/SayHello",
                () -> new Route("/pkg.Greeter/SayHello", (c, r) -> ServiceResponse.ok()));

        var data = new GrpcRoutingData(routes);
        routes.clear();

        assertEquals(1, data.routes().size());
        assertTrue(data.routes().containsKey("/pkg.Greeter/SayHello"));
    }
}
