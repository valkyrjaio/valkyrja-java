/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteParameterException;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link DynamicRoute}. */
final class DynamicRouteTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private static DynamicRoute route() {
        return new DynamicRoute(
                "/users/{id}",
                "users.show",
                "/users/(\\d+)",
                List.of(new Parameter("id", "\\d+")),
                HANDLER);
    }

    @Test
    void exposesRegexAndParameters() {
        var route = route();

        assertEquals("/users/(\\d+)", route.getRegex());
        assertEquals(1, route.getParameters().size());
        assertTrue(route.hasParameter("id"));
        assertEquals("id", route.getParameter("id").getName());
    }

    @Test
    void getParameterThrowsForUnknown() {
        assertThrows(
                HttpRoutingInvalidRouteParameterException.class, () -> route().getParameter("nope"));
        assertFalse(route().hasParameter("nope"));
    }

    @Test
    void withRegexAndParameterMutations() {
        var route = route();

        assertEquals("/x", route.withRegex("/x").getRegex());

        var replaced = route.withParameters(new Parameter("slug", "\\w+"));
        assertTrue(((DynamicRoute) replaced).hasParameter("slug"));

        var added =
                route.withAddedParameters(
                        (ParameterContract) new Parameter("page", "\\d+"));
        assertEquals(2, ((DynamicRoute) added).getParameters().size());
    }

    @Test
    void inheritsRouteBehavior() {
        // The path is filtered by the inherited Route logic.
        assertEquals("users.show", route().getName());
    }
}