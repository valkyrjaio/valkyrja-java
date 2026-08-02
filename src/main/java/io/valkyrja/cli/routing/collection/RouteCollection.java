/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.collection;

import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidRouteNameException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RouteCollection implements RouteCollectionContract {

    protected final Map<String, Supplier<RouteContract>> routes = new LinkedHashMap<>();

    @Override
    public RouteCollection add(RouteContract... commands) {
        for (RouteContract command : commands) {
            routes.put(command.getName(), () -> command);
        }
        return this;
    }

    @Override
    public RouteContract get(String name) {
        Supplier<RouteContract> route = routes.get(name);
        if (route != null) {
            return route.get();
        }
        throw new CliRoutingInvalidRouteNameException("The route `" + name + "` was not found.");
    }

    @Override
    public boolean has(String name) {
        return routes.containsKey(name);
    }

    @Override
    public Map<String, RouteContract> all() {
        Map<String, RouteContract> result = new LinkedHashMap<>();
        for (var entry : routes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }

        return result;
    }
}
