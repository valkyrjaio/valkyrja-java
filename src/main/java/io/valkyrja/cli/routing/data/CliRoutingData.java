/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.routing.data.contract.CliRoutingDataContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Supplier;

public record CliRoutingData(Map<String, Supplier<RouteContract>> routes)
        implements CliRoutingDataContract {

    public CliRoutingData {
        routes = Map.copyOf(routes);
    }

    public CliRoutingData() {
        this(Map.of());
    }
}
