/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.collection;

import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The service map keyed by fully-qualified method name. A direct {@code Map} lookup resolves an
 * inbound call to its {@link RouteContract} — no pattern matching, the same shape CLI uses for
 * commands.
 */
public class RouteCollection implements RouteCollectionContract {

    protected final Map<String, RouteContract> routes = new LinkedHashMap<>();

    @Override
    public RouteCollection add(RouteContract... routes) {
        for (RouteContract route : routes) {
            this.routes.put(route.getMethod(), route);
        }
        return this;
    }

    @Override
    public RouteContract get(String method) {
        RouteContract route = routes.get(method);
        if (route != null) {
            return route;
        }
        throw new GrpcRoutingInvalidMethodException("The route `" + method + "` was not found.");
    }

    @Override
    public boolean has(String method) {
        return routes.containsKey(method);
    }

    @Override
    public Map<String, RouteContract> all() {
        return new LinkedHashMap<>(routes);
    }
}
