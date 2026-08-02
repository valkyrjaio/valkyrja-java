/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.routing.data.CliRoutingData;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingData} record. */
final class CliRoutingDataTest {

    @Test
    void defaultHasNoRoutes() {
        assertTrue(new CliRoutingData().routes().isEmpty());
    }

    @Test
    void routesAccessorAndDefensiveCopy() {
        Map<String, Supplier<RouteContract>> routes = new HashMap<>();
        routes.put("list", () -> new Route("list", "List", (c, r) -> new EmptyOutput()));

        var data = new CliRoutingData(routes);
        routes.clear();

        assertEquals(1, data.routes().size());
        assertTrue(data.routes().containsKey("list"));
    }
}
