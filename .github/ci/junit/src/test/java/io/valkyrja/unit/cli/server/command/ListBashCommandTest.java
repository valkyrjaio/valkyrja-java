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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.server.command.ListBashCommand;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link ListBashCommand}. */
final class ListBashCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollectionContract collection;
    private RouteContract route;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = mock(RouteCollectionContract.class);
        route = mock(RouteContract.class);
    }

    private ListBashCommand command() {
        return new ListBashCommand(route, collection, outputFactory);
    }

    private static Map<String, RouteContract> routeMap(String... names) {
        Map<String, RouteContract> map = new LinkedHashMap<>();
        for (String name : names) {
            map.put(name, new Route(name, name, (c, r) -> new EmptyOutput()));
        }
        return map;
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(ListBashCommand.help().getText().isEmpty());
    }

    @Test
    void listsAllRouteNamesWhenNoNamespace() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));
        when(route.hasArgument("namespace")).thenReturn(false);

        var output = command().run();

        assertEquals("build deploy", output.getMessages().get(0).getText());
    }

    @Test
    void stripsNamespacePrefixWhenColonPresent() {
        when(collection.all()).thenReturn(routeMap("db:migrate", "db:seed", "build"));
        when(route.hasArgument("namespace")).thenReturn(true);
        var nsArg = mock(ArgumentParameterContract.class);
        when(nsArg.getFirstValue()).thenReturn("db:");
        when(route.getArgument("namespace")).thenReturn(nsArg);

        var output = command().run();

        assertEquals("migrate seed", output.getMessages().get(0).getText());
    }

    @Test
    void filtersByPrefixWithoutColon() {
        when(collection.all()).thenReturn(routeMap("build", "deploy", "destroy"));
        when(route.hasArgument("namespace")).thenReturn(true);
        var nsArg = mock(ArgumentParameterContract.class);
        when(nsArg.getFirstValue()).thenReturn("de");
        when(route.getArgument("namespace")).thenReturn(nsArg);

        var output = command().run();

        assertEquals("deploy destroy", output.getMessages().get(0).getText());
    }
}