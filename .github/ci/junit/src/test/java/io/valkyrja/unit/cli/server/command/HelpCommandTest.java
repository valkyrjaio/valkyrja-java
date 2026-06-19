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
import io.valkyrja.cli.interaction.message.Message;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link HelpCommand}. */
final class HelpCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteCollection collection;
    private RouteContract route;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        lenient().when(outputFactory.createOutput()).thenReturn(new Output());
        collection = new RouteCollection();
        route = mock(RouteContract.class);
        lenient().when(route.getName()).thenReturn("help");
        lenient().when(route.getDescription()).thenReturn("Help");
    }

    private HelpCommand command() {
        return new HelpCommand("MyApp", "1.0", route, collection, outputFactory);
    }

    private void whenAskingFor(String commandName) {
        var commandOption = mock(OptionParameterContract.class);
        when(commandOption.getFirstValue()).thenReturn(commandName);
        when(route.getOption("command")).thenReturn(commandOption);
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(HelpCommand.help().getText().isEmpty());
    }

    @Test
    void runReturnsErrorWhenCommandNotFound() {
        whenAskingFor("ghost");

        var output = command().run();

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
        whenAskingFor("deploy");

        var output = command().run();

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
        whenAskingFor("ping");

        var output = command().run();

        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("ping")));
        // Global options are always shown even when the command has none of its own.
        assertTrue(output.getMessages().stream().anyMatch(m -> m.getText().contains("--quiet")));
    }
}
