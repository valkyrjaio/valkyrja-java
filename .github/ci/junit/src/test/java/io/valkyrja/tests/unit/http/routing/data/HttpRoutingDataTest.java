/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.routing.data.HttpRoutingData;
import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingData} record. */
final class HttpRoutingDataTest {

    @Test
    void defaultIsEmpty() {
        var data = new HttpRoutingData();

        assertTrue(data.routes().isEmpty());
        assertTrue(data.paths().isEmpty());
        assertTrue(data.dynamicPaths().isEmpty());
        assertTrue(data.regexes().isEmpty());
    }

    @Test
    void accessorsExposeMaps() {
        Map<String, Supplier<RouteContract>> routes = Map.of();
        var data =
                new HttpRoutingData(
                        routes,
                        Map.of("GET", Map.of("/", "home")),
                        Map.of(),
                        Map.of("GET", Map.of("/x", "\\d+")));

        assertEquals(1, data.paths().size());
        assertEquals(1, data.regexes().size());
    }
}
