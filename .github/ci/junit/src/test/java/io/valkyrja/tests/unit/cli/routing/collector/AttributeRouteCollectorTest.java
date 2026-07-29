/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.collector.AttributeRouteCollector;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.tests.fixtures.cli.routing.AnnotatedController;
import io.valkyrja.tests.fixtures.cli.routing.CliRoutingCombinationsController;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link AttributeRouteCollector}. */
final class AttributeRouteCollectorTest {

    private Map<String, RouteContract> routes;

    @BeforeEach
    void setUp() {
        var collected = new AttributeRouteCollector().getRoutes(AnnotatedController.class);
        routes =
                collected.stream()
                        .collect(Collectors.toMap(RouteContract::getName, Function.identity()));
    }

    @Test
    void collectsAllAnnotatedRoutes() {
        assertEquals(3, routes.size());
    }

    @Test
    void appliesClassAndMethodNamePrefixes() {
        // @Name("ctrl") on the class + @Name("method") on the method wrap the route name "run".
        assertTrue(routes.containsKey("ctrl.run.method"));
    }

    @Test
    void collectsArgumentsOptionsAndMiddleware() {
        var route = routes.get("ctrl.run.method");

        assertTrue(route.hasArgument("target"));
        assertTrue(route.hasOption("verbose"));
        // PassThroughMiddleware implements all four contracts, so every middleware list is
        // populated.
        assertEquals(1, route.getRouteMatchedMiddleware().size());
        assertEquals(1, route.getRouteDispatchedMiddleware().size());
        assertEquals(1, route.getThrowableCaughtMiddleware().size());
        assertEquals(1, route.getProcessExitingMiddleware().size());
    }

    @Test
    void routeHandlerDelegatesToConfiguredHandler() {
        var route = routes.get("ctrl.run.method");
        var container = mock(ContainerContract.class);

        OutputContract output = route.getHandler().apply(container, route);

        assertInstanceOf(OutputContract.class, output);
    }

    @Test
    void defaultHandlerProducesOutputWhenNoRouteHandlerAnnotation() {
        var route = routes.get("ctrl.plain");
        var container = mock(ContainerContract.class);

        assertInstanceOf(OutputContract.class, route.getHandler().apply(container, route));
    }

    @Test
    void handlerWrapsReflectionFailures() {
        var route = routes.get("ctrl.fail");
        var container = mock(ContainerContract.class);

        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void emptyClassListYieldsNoRoutes() {
        assertTrue(new AttributeRouteCollector().getRoutes().isEmpty());
    }

    @Test
    void collectsControllerWithoutClassNameAndUnrelatedMiddleware() {
        var routes =
                new AttributeRouteCollector()
                        .getRoutes(io.valkyrja.tests.fixtures.cli.routing.PlainController.class);

        assertTrue(routes.stream().anyMatch(route -> route.getName().equals("plain")));
    }

    @Test
    void convertsArgumentAndOptionPermutations() {
        var collected =
                new AttributeRouteCollector().getRoutes(CliRoutingCombinationsController.class);

        assertEquals(1, collected.size());
        var route = collected.get(0);

        // Required, single-value argument.
        var required = route.getArgument("required");
        assertEquals(ArgumentMode.REQUIRED, required.getMode());
        assertEquals(ArgumentValueMode.DEFAULT, required.getValueMode());

        // Optional, array argument.
        var rest = route.getArgument("rest");
        assertEquals(ArgumentMode.OPTIONAL, rest.getMode());
        assertEquals(ArgumentValueMode.ARRAY, rest.getValueMode());

        // Required, single-value option carrying short names, valid values, default, display name.
        var format = route.getOption("format");
        assertEquals(OptionMode.REQUIRED, format.getMode());
        assertEquals(OptionValueMode.DEFAULT, format.getValueMode());
        assertEquals(List.of("f"), format.getShortNames());
        assertEquals(List.of("json", "xml"), format.getValidValues());
        assertEquals("json", format.getDefaultValue());
        assertEquals("fmt", format.getValueDisplayName());

        // Valueless (NONE) flag option.
        var flag = route.getOption("flag");
        assertEquals(OptionMode.OPTIONAL, flag.getMode());
        assertEquals(OptionValueMode.NONE, flag.getValueMode());

        // Repeatable (ARRAY) option.
        var tags = route.getOption("tags");
        assertEquals(OptionValueMode.ARRAY, tags.getValueMode());
    }
}
