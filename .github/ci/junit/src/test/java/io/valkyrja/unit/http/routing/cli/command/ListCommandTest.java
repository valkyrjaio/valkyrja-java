/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.cli.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http routing cli {@link ListCommand}. */
final class ListCommandTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private OutputFactoryContract outputFactory;
    private RouteCollectionContract collection;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = mock(RouteCollectionContract.class);
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(ListCommand.help().getText().isEmpty());
    }

    @Test
    void runWithRoutesListsPathsNamesAndRegex() {
        Map<String, RouteContract> routes = new LinkedHashMap<>();
        routes.put("/users", new Route("/users", "users.index", HANDLER));
        routes.put(
                "/users/{id}",
                new DynamicRoute(
                        "/users/{id}",
                        "users.show",
                        "/users/(\\d+)",
                        List.of(),
                        HANDLER));
        when(collection.getAll(RequestMethod.ANY)).thenReturn(routes);

        var output = new ListCommand(collection, outputFactory).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("/users")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("users.index")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("/users/(\\d+)")));
    }

    @Test
    void runWithNoRoutesReturnsError() {
        when(collection.getAll(RequestMethod.ANY)).thenReturn(Map.of());

        var output = new ListCommand(collection, outputFactory).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
    }
}
