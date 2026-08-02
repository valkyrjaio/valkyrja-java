/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.data;

import io.valkyrja.http.routing.data.contract.HttpRoutingDataContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Supplier;

public record HttpRoutingData(
        Map<String, Supplier<RouteContract>> routes,
        Map<String, Map<String, String>> paths,
        Map<String, Map<String, String>> dynamicPaths,
        Map<String, Map<String, String>> regexes)
        implements HttpRoutingDataContract {
    public HttpRoutingData() {
        this(Map.of(), Map.of(), Map.of(), Map.of());
    }
}
