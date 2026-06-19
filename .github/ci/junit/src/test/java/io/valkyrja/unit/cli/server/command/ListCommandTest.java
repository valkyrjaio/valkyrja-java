/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.server.command.ListCommand;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link ListCommand}. */
final class ListCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollectionContract collection;
    private RouteContract route;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = mock(RouteCollectionContract.class);
        route = mock(RouteContract.class);
        lenient().when(route.getName()).thenReturn("list");
        lenient().when(route.getDescription()).thenReturn("List");
    }

    private ListCommand command() {
        return new ListCommand("MyApp", "1.0", route, collection, outputFactory);
    }

    private static Map<String, RouteContract> routeMap(String... names) {
        Map<String, RouteContract> map = new LinkedHashMap<>();
        for (String name : names) {
            map.put(name, new Route(name, name + " description", (c, r) -> new EmptyOutput()));
        }
        return map;
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(ListCommand.help().getText().isEmpty());
    }

    @Test
    void runListsAllRoutesWhenNoNamespace() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));
        when(route.hasOption("namespace")).thenReturn(false);

        var output = command().run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("build")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("deploy")));
    }

    @Test
    void runFiltersByNamespace() {
        when(collection.all()).thenReturn(routeMap("db:migrate", "build"));
        when(route.hasOption("namespace")).thenReturn(true);
        var nsOption = mock(OptionParameterContract.class);
        when(nsOption.getFirstValue()).thenReturn("db:");
        when(route.getOption("namespace")).thenReturn(nsOption);

        var output = command().run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("db:migrate")));
        assertFalse(output.getMessages().stream().anyMatch(m -> m.getText().contains("build")));
    }

    @Test
    void runWithNoRoutesReturnsError() {
        when(collection.all()).thenReturn(routeMap());
        when(route.hasOption("namespace")).thenReturn(false);

        var output = command().run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("No routes")));
    }

    @Test
    void runWithUnknownNamespaceReturnsError() {
        when(collection.all()).thenReturn(routeMap("build"));
        when(route.hasOption("namespace")).thenReturn(true);
        var nsOption = mock(OptionParameterContract.class);
        when(nsOption.getFirstValue()).thenReturn("missing:");
        when(route.getOption("namespace")).thenReturn(nsOption);

        var output = command().run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(
                output.getMessages().stream()
                        .anyMatch(m -> m.getText().contains("missing:")));
    }
}
