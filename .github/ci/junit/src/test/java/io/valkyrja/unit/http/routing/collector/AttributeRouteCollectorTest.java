/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.fixtures.http.routing.AnnotatedHttpController;
import io.valkyrja.fixtures.http.routing.RoutingCombinationsHttpController;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collector.AttributeRouteCollector;
import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http {@link AttributeRouteCollector}. */
final class AttributeRouteCollectorTest {

    private Map<String, RouteContract> routes;

    @BeforeEach
    void setUp() {
        var collected = new AttributeRouteCollector().getRoutes(AnnotatedHttpController.class);
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
    void handlerInvokesControllerMethod() {
        var route = routes.get("api.plain");
        var container = mock(ContainerContract.class);

        ResponseContract response = route.getHandler().apply(container, route);

        assertInstanceOf(ResponseContract.class, response);
    }

    @Test
    void handlerWrapsControllerFailures() {
        var route = routes.get("api.boom");
        var container = mock(ContainerContract.class);

        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void emptyClassListYieldsNoRoutes() {
        assertTrue(new AttributeRouteCollector().getRoutes().isEmpty());
    }

    @Test
    void collectsControllerWithoutClassAnnotationsAndUnrelatedMiddleware() {
        var routes =
                new AttributeRouteCollector()
                        .getRoutes(io.valkyrja.fixtures.http.routing.PlainHttpController.class);

        assertTrue(routes.stream().anyMatch(route -> route.getName().equals("plain")));
    }

    @Test
    void annotationPathProducesSameRegexAsDirectConstruction() {
        var byName =
                new AttributeRouteCollector()
                        .getRoutes(RoutingCombinationsHttpController.class).stream()
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
}
