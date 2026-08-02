/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidDynamicRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteNameException;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/**
 * Exercises the protected defensive guards on {@link RouteCollection} that are unreachable through
 * the public API. Placed in the source package to reach the protected methods directly.
 */
final class RouteCollectionInternalsTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    @Test
    void setRouteToRequestMethodIgnoresAnyMethod() {
        var collection = new RouteCollection();

        collection.setRouteToRequestMethod(new Route("/x", "x.name", HANDLER), RequestMethod.ANY);

        assertFalse(collection.hasPath("/x", RequestMethod.GET));
    }

    @Test
    void getRouteFromNameRejectsUnknownName() {
        var collection = new RouteCollection();

        assertThrows(
                HttpRoutingInvalidRouteNameException.class,
                () -> collection.getRouteFromName("missing"));
    }

    @Test
    void getDynamicRouteFromNameRejectsStaticRoute() {
        var collection = new RouteCollection();
        collection.add(new Route("/x", "x.name", HANDLER));

        assertThrows(
                HttpRoutingInvalidDynamicRouteNameException.class,
                () -> collection.getDynamicRouteFromName("x.name"));
    }
}
