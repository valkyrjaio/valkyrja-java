/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.collector;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.classes.http.routing.AnnotatedHttpController;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collector.AttributeRouteCollector;
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
}
