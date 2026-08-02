/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteRegexException;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link RouteCollection}. */
final class RouteCollectionTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private RouteCollection collection;

    @BeforeEach
    void setUp() {
        collection = new RouteCollection();
        collection.add(new Route("/users", "users.index", HANDLER));
        collection.add(
                new DynamicRoute(
                        "/users/{id}",
                        "users.show",
                        "/users/(\\d+)",
                        List.of(new Parameter("id", "\\d+")),
                        HANDLER));
    }

    @Test
    void staticRouteByPathAndName() {
        assertTrue(collection.hasPath("/users", RequestMethod.GET));
        assertEquals("users.index", collection.getByPath("/users", RequestMethod.GET).getName());
        assertTrue(collection.hasName("users.index"));
        assertEquals("/users", collection.getByName("users.index").getPath());
    }

    @Test
    void dynamicRouteByRegex() {
        assertTrue(collection.hasRegex("/users/(\\d+)", RequestMethod.GET));
        assertEquals(
                "users.show", collection.getByRegex("/users/(\\d+)", RequestMethod.GET).getName());
    }

    @Test
    void lookupAcrossAnyMethod() {
        assertTrue(collection.hasPath("/users", RequestMethod.ANY));
        assertEquals("users.index", collection.getByPath("/users", RequestMethod.ANY).getName());
        assertTrue(collection.hasRegex("/users/(\\d+)", RequestMethod.ANY));
        assertEquals(
                "users.show", collection.getByRegex("/users/(\\d+)", RequestMethod.ANY).getName());
    }

    @Test
    void anyLookupThrowsWhenNoMethodMatches() {
        assertThrows(
                HttpRoutingInvalidRoutePathException.class,
                () -> collection.getByPath("/none", RequestMethod.ANY));
        assertThrows(
                HttpRoutingInvalidRouteRegexException.class,
                () -> collection.getByRegex("/none", RequestMethod.ANY));
    }

    @Test
    void missingLookupsThrow() {
        assertThrows(
                HttpRoutingInvalidRoutePathException.class,
                () -> collection.getByPath("/nope", RequestMethod.GET));
        assertThrows(
                HttpRoutingInvalidRouteRegexException.class,
                () -> collection.getByRegex("/nope", RequestMethod.GET));
        assertThrows(
                HttpRoutingInvalidRouteNameException.class, () -> collection.getByName("nope"));
    }

    @Test
    void pathsRegexesAndGetAll() {
        assertTrue(collection.getPaths(RequestMethod.GET).containsKey("/users"));
        assertFalse(collection.getPaths(RequestMethod.ANY).isEmpty());
        assertTrue(collection.getRegexes(RequestMethod.GET).containsKey("/users/(\\d+)"));
        assertFalse(collection.getRegexes(RequestMethod.ANY).isEmpty());
        assertTrue(collection.getAll(RequestMethod.GET).containsKey("/users"));
    }

    @Test
    void anyMethodRouteIsRegisteredForAllMethods() {
        var collection = new RouteCollection();
        collection.add(new Route("/wild", "wild", HANDLER).withRequestMethods(RequestMethod.ANY));

        assertTrue(collection.hasPath("/wild", RequestMethod.POST));
        assertTrue(collection.hasPath("/wild", RequestMethod.DELETE));
    }

    @Test
    void dataRoundTrips() {
        var data = collection.getData();
        var restored = new RouteCollection();

        restored.setFromData(data);

        assertTrue(restored.hasName("users.index"));
        assertTrue(restored.hasPath("/users", RequestMethod.GET));
    }

    @Test
    void hasPathAndHasRegexAreFalseForMissingEntries() {
        assertFalse(collection.hasPath("/missing", RequestMethod.GET));
        assertFalse(collection.hasRegex("/missing/(\\d+)", RequestMethod.GET));
    }

    @Test
    void hasPathFindsDynamicRouteByItsPath() {
        assertTrue(collection.hasPath("/users/{id}", RequestMethod.GET));
    }

    @Test
    void hasPathFalseWhenMethodHasNoPaths() {
        assertFalse(collection.hasPath("/users", RequestMethod.POST));
    }

    @Test
    void hasRegexFalseWhenMethodHasNoRegexes() {
        assertFalse(collection.hasRegex("/users/(\\d+)", RequestMethod.POST));
    }
}
