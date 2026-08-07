/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collector.AttributeRouteCollector;
import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.tests.fixtures.http.routing.AnnotatedHttpControllerFixture;
import io.valkyrja.tests.fixtures.http.routing.RoutingCombinationsHttpControllerFixture;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http {@link AttributeRouteCollector}. */
final class AttributeRouteCollectorTest {

    private Map<String, RouteContract> routes;

    /** A container that binds the controller, the way an application binds its own. */
    private static Container containerWithController() {
        var container = new Container();
        container.bind(
                AnnotatedHttpControllerFixture.class,
                (c, a) -> new AnnotatedHttpControllerFixture());

        return container;
    }

    @BeforeEach
    void setUp() {
        var collected =
                new AttributeRouteCollector().getRoutes(AnnotatedHttpControllerFixture.class);
        routes =
                collected.stream()
                        .collect(Collectors.toMap(RouteContract::getName, Function.identity()));
    }

    @Test
    void collectsStaticDynamicAndMultiRoutes() {
        // index, plain, a, b, boom (static) + show (dynamic).
        assertTrue(routes.size() >= 6);
    }

    @Test
    void appliesClassAndMethodPathAndName() {
        var route = routes.get("api.index.list");

        assertNotNull(route);
        assertTrue(route.getPath().contains("/api"));
        assertTrue(route.hasRequestStruct());
        assertTrue(route.hasResponseStruct());
        assertTrue(route.getRouteMatchedMiddleware().size() >= 1);
    }

    @Test
    void routeWithoutStructsHasNone() {
        var route = routes.get("api.plain");

        assertNotNull(route);
        assertTrue(!route.hasRequestStruct());
    }

    @Test
    void handlerResolvesTheControllerFromTheContainer() {
        var route = routes.get("api.plain");
        // A real container, because the controller is now resolved through it rather than through
        // its no-argument constructor — which a controller with dependencies does not have.
        var container = containerWithController();

        ResponseContract response = route.getHandler().apply(container, route);

        assertInstanceOf(ResponseContract.class, response);
    }

    @Test
    void handlerWrapsControllerFailures() {
        var route = routes.get("api.boom");
        var container = containerWithController();

        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void handlerNamedByAnnotationIsInvokedInsteadOfTheControllerMethod() {
        // The controller method takes no arguments, so it could not be invoked as a handler at all;
        // the @RouteHandler names the real handler, mirroring the generated routing data.
        var route = routes.get("api.handled");
        var container = containerWithController();

        ResponseContract response = route.getHandler().apply(container, route);

        assertInstanceOf(ResponseContract.class, response);
    }

    @Test
    void handlerNamedByAnnotationWrapsAMissingHandlerMethod() {
        var route = routes.get("api.handled.missing");
        var container = containerWithController();

        var exception =
                assertThrows(
                        RuntimeException.class, () -> route.getHandler().apply(container, route));

        assertTrue(exception.getMessage().contains("doesNotExist"));
    }

    @Test
    void emptyClassListYieldsNoRoutes() {
        assertTrue(new AttributeRouteCollector().getRoutes().isEmpty());
    }

    @Test
    void collectsControllerWithoutClassAnnotationsAndUnrelatedMiddleware() {
        var routes =
                new AttributeRouteCollector()
                        .getRoutes(
                                io.valkyrja.tests.fixtures.http.routing.PlainHttpControllerFixture
                                        .class);

        assertTrue(routes.stream().anyMatch(route -> route.getName().equals("plain")));
    }

    @Test
    void annotationPathProducesSameRegexAsDirectConstruction() {
        var byName =
                new AttributeRouteCollector()
                        .getRoutes(RoutingCombinationsHttpControllerFixture.class).stream()
                                .collect(
                                        Collectors.toMap(
                                                RouteContract::getName, Function.identity()));

        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "num"
                        + Regex.PATH
                        + "(?<id>"
                        + Regex.NUM
                        + ")"
                        + Regex.END,
                regexOf(byName, "combinations.num"));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "slug"
                        + Regex.PATH
                        + "(?<slug>"
                        + Regex.SLUG
                        + ")"
                        + Regex.END,
                regexOf(byName, "combinations.slug"));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "optional"
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?<opt>"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.END,
                regexOf(byName, "combinations.optional"));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "nc"
                        + Regex.PATH
                        + "(?:"
                        + Regex.ALPHA
                        + ")"
                        + Regex.END,
                regexOf(byName, "combinations.nonCapture"));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "multi"
                        + Regex.PATH
                        + "(?<x>"
                        + Regex.NUM
                        + ")"
                        + Regex.PATH
                        + "(?<y>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.END,
                regexOf(byName, "combinations.multi"));
    }

    private static String regexOf(Map<String, RouteContract> byName, String name) {
        var route = byName.get(name);
        assertInstanceOf(DynamicRouteContract.class, route);
        return ((DynamicRouteContract) route).getRegex();
    }

    @Test
    void promotesAPlainRouteWhosePathCarriesAParameterIntoADynamicRoute() {
        var byName =
                new AttributeRouteCollector()
                        .getRoutes(RoutingCombinationsHttpControllerFixture.class).stream()
                                .collect(
                                        Collectors.toMap(
                                                RouteContract::getName, Function.identity()));

        var route = byName.get("combinations.promoted");

        // Declared with a plain @Route, it must still be dynamic and carry its parameter's regex.
        var dynamic = assertInstanceOf(DynamicRouteContract.class, route);
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "promoted"
                        + Regex.PATH
                        + "(?<id>"
                        + Regex.NUM
                        + ")"
                        + Regex.END,
                dynamic.getRegex());
        assertEquals(1, dynamic.getParameters().size());
        assertEquals("id", dynamic.getParameters().get(0).getName());
    }
}
