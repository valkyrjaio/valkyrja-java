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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.server.command.ListCommand;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link ListCommand}. */
final class ListCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollectionContract collection;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = mock(RouteCollectionContract.class);
    }

    private ListCommand command(RouteContract route) {
        return new ListCommand("MyApp", "1.0", route, collection, outputFactory);
    }

    /**
     * Builds a route that declares the namespace option. The option carries a value only when the
     * caller spelled it. The router keeps every declared parameter on the route, so the option is
     * present whether or not the command line spelled it.
     */
    private RouteContract route(String spelledValue) {
        List<OptionContract> options =
                spelledValue.isEmpty()
                        ? new ArrayList<>()
                        : List.of(new Option("namespace", spelledValue, OptionType.LONG));

        OptionParameterContract namespace =
                new OptionParameter(
                        "namespace",
                        "An optional namespace to filter commands by",
                        "namespace",
                        "",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        options,
                        OptionMode.OPTIONAL,
                        OptionValueMode.DEFAULT);

        return new Route(
                "list",
                "List",
                (container, matched) -> new EmptyOutput(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(namespace));
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
    void runListsAllRoutesWhenNamespaceIsNotSpelled() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));

        OutputContract output = command(route("")).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("build")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("deploy")));
    }

    @Test
    void runFiltersByNamespace() {
        when(collection.all()).thenReturn(routeMap("db:migrate", "build"));

        OutputContract output = command(route("db:")).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("db:migrate")));
        assertFalse(output.getMessages().stream().anyMatch(m -> m.getText().contains("build")));
    }

    @Test
    void runWithNoRoutesReturnsError() {
        when(collection.all()).thenReturn(routeMap());

        OutputContract output = command(route("")).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("No routes")));
    }

    @Test
    void runWithUnknownNamespaceReturnsError() {
        when(collection.all()).thenReturn(routeMap("build"));

        OutputContract output = command(route("missing:")).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("missing:")));
    }

    /** A route that declares no option filters nothing instead of throwing. */
    @Test
    void runWithARouteThatDeclaresNoOptionsListsEveryRoute() {
        when(collection.all()).thenReturn(routeMap("build", "deploy"));
        RouteContract route = new Route("list", "List", (c, r) -> new EmptyOutput());

        OutputContract output = command(route).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("build")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("deploy")));
    }
}
