/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.server.command.ListBashCommand;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link ListBashCommand}. */
final class ListBashCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollectionContract collection;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = mock(RouteCollectionContract.class);
    }

    private ListBashCommand command(RouteContract route) {
        return new ListBashCommand(route, collection, outputFactory);
    }

    /**
     * A route that declares the namespace argument, carrying a value only where the caller spelled
     * it. The router keeps every declared parameter on the route, so the argument is present
     * whether or not the command line spelled it.
     */
    private RouteContract route(String spelledValue) {
        List<ArgumentContract> arguments =
                spelledValue.isEmpty() ? new ArrayList<>() : List.of(new Argument(spelledValue));

        ArgumentParameterContract namespace =
                new ArgumentParameter(
                        "namespace",
                        "An optional namespace to filter commands by",
                        ArgumentMode.OPTIONAL,
                        ArgumentValueMode.DEFAULT,
                        arguments);

        return new Route(
                "list:bash",
                "List for bash",
                (container, matched) -> new EmptyOutput(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(namespace),
                new ArrayList<>());
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
    void listsAllRouteNamesWhenNamespaceIsNotSpelled() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));

        OutputContract output = command(route("")).run();

        assertEquals("build deploy", output.getMessages().get(0).getText());
    }

    @Test
    void stripsNamespacePrefixWhenColonPresent() {
        when(collection.all()).thenReturn(routeMap("db:migrate", "db:seed", "build"));

        OutputContract output = command(route("db:")).run();

        assertEquals("migrate seed", output.getMessages().get(0).getText());
    }

    @Test
    void filtersByPrefixWithoutColon() {
        when(collection.all()).thenReturn(routeMap("build", "deploy", "destroy"));

        OutputContract output = command(route("de")).run();

        assertEquals("deploy destroy", output.getMessages().get(0).getText());
    }

    /** A route that declares no argument filters nothing instead of throwing. */
    @Test
    void runWithARouteThatDeclaresNoArgumentsListsEveryRoute() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));
        RouteContract route = new Route("list:bash", "List for bash", (c, r) -> new EmptyOutput());

        OutputContract output = command(route).run();

        assertEquals("build deploy", output.getMessages().get(0).getText());
    }
}
