/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link Processor}. */
final class ProcessorTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private final Processor processor = new Processor();

    @Test
    void normalizesStaticPath() {
        var route = processor.route(new Route("//users//", "users", HANDLER));

        assertEquals("/users", route.getPath());
    }

    @Test
    void buildsRegexForDynamicRouteWithCapturingParameter() {
        var dynamic =
                new DynamicRoute(
                        "/{id}",
                        "show",
                        "",
                        List.of(new Parameter("id", "\\d+")),
                        HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertFalse(processed.getRegex().isEmpty());
    }

    @Test
    void keepsExistingRegex() {
        var dynamic =
                new DynamicRoute(
                        "/{id}",
                        "show",
                        "^/(?<id>\\d+)$",
                        List.of(new Parameter("id", "\\d+")),
                        HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertEquals("^/(?<id>\\d+)$", processed.getRegex());
    }

    @Test
    void dynamicRouteWithoutPlaceholderIsReturnedUnchanged() {
        var dynamic =
                new DynamicRoute("/static", "static", "", List.of(new Parameter("x", "\\d+")), HANDLER);

        assertEquals("/static", processor.route(dynamic).getPath());
    }

    @Test
    void throwsWhenParameterPlaceholderIsMissingFromPath() {
        var dynamic =
                new DynamicRoute(
                        "/{id}",
                        "show",
                        "",
                        List.of((ParameterContract) new Parameter("other", "\\d+")),
                        HANDLER);

        assertThrows(HttpRoutingInvalidRoutePathException.class, () -> processor.route(dynamic));
    }
}