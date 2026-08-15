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

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.server.command.HelpCommand;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link HelpCommand}. */
final class HelpCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollection collection;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = new RouteCollection();
    }

    private HelpCommand command(RouteContract route) {
        return new HelpCommand("MyApp", "1.0", route, collection, outputFactory);
    }

    /**
     * Builds a help route that declares the command option. The option carries a value only when
     * the caller spelled it. The router keeps every declared parameter on the route, so the option
     * is present whether or not the command line spelled it.
     */
    private RouteContract routeAskingFor(String commandName) {
        List<OptionContract> options =
                commandName.isEmpty()
                        ? new ArrayList<>()
                        : List.of(new Option("command", commandName, OptionType.LONG));

        OptionParameterContract commandOption =
                new OptionParameter(
                        "command",
                        "The name of the command to get help for",
                        "command",
                        "",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        options,
                        OptionMode.OPTIONAL,
                        OptionValueMode.DEFAULT);

        return new Route(
                "help",
                "Help",
                (container, matched) -> new EmptyOutput(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(commandOption));
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(HelpCommand.help().getText().isEmpty());
    }

    /** A route that declares no command option reports the miss instead of throwing. */
    @Test
    void runReturnsErrorWhenTheRouteDeclaresNoCommandOption() {
        RouteContract route = new Route("help", "Help", (c, r) -> new EmptyOutput());

        var output = command(route).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(
                output.getMessages().stream()
                        .anyMatch(m -> m.getText().contains("Command `` was not found.")));
    }

    /** A declared command option that the caller left unspelled reports the same miss. */
    @Test
    void runReturnsErrorWhenTheCommandOptionIsNotSpelled() {
        var output = command(routeAskingFor("")).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(
                output.getMessages().stream()
                        .anyMatch(m -> m.getText().contains("Command `` was not found.")));
    }

    @Test
    void runReturnsErrorWhenCommandNotFound() {
        var output = command(routeAskingFor("ghost")).run();

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("ghost")));
    }

    @Test
    void runRendersRichCommandHelp() {
        var richOption =
                (OptionParameter)
                        new OptionParameter("verbose", "Verbose output")
                                .withShortNames("v")
                                .withValueDisplayName("LEVEL")
                                .withValueMode(OptionValueMode.ARRAY)
                                .withMode(OptionMode.REQUIRED)
                                .withValidValues("low", "high")
                                .withDefaultValue("low");
        var optionalOption =
                (OptionParameter)
                        new OptionParameter("env", "Environment").withValueDisplayName("ENV");
        var plainOption = new OptionParameter("force", "Force the action");
        var arrayArg =
                (ArgumentParameter)
                        new ArgumentParameter("files", "Files to deploy")
                                .withValueMode(ArgumentValueMode.ARRAY);
        var deploy =
                new Route(
                                "deploy",
                                "Deploy the application to the configured remote target using the"
                                        + " selected strategy and options provided on the command"
                                        + " line for this run.",
                                (c, r) -> new EmptyOutput())
                        .withArguments(new ArgumentParameter("target", "Target host"), arrayArg)
                        .withOptions(richOption, optionalOption, plainOption)
                        .withHelpText(() -> new Message("Extended help text."));
        collection.add(deploy);

        var output = command(routeAskingFor("deploy")).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("deploy")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("--verbose")));
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("(default)")));
        assertTrue(
                output.getMessages().stream()
                        .anyMatch(m -> m.getText().contains("Extended help text.")));
    }

    @Test
    void runRendersBareCommandHelp() {
        collection.add(new Route("ping", "Ping the server", (c, r) -> new EmptyOutput()));

        var output = command(routeAskingFor("ping")).run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("ping")));
        // Global options are always shown even when the command has none of its own.
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("--quiet")));
    }
}
