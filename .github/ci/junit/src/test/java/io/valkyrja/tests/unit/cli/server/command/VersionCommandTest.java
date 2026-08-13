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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.server.command.VersionCommand;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link VersionCommand}. */
final class VersionCommandTest {

    private OutputFactoryContract outputFactory;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        when(outputFactory.createOutput()).thenReturn(new Output());
    }

    private VersionCommand command(RouteContract route) {
        return new VersionCommand(outputFactory, "MyApp", "1.2.3", route);
    }

    /**
     * Builds a route that declares the short option and the plain option. Each option carries a
     * value only when the caller spelled it. The router keeps every declared parameter on the
     * route, so a declared option is present whether or not the command line spelled it.
     */
    private RouteContract route(String... spelled) {
        List<String> spelledNames = List.of(spelled);

        return new Route(
                "version",
                "Show version",
                (container, matched) -> new Output(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(option("short", spelledNames), option("plain", spelledNames)));
    }

    private OptionParameterContract option(String name, List<String> spelledNames) {
        List<OptionContract> options =
                spelledNames.contains(name)
                        ? List.of(new Option(name, OptionType.LONG))
                        : new ArrayList<>();

        return new OptionParameter(
                name,
                "The " + name + " option",
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>(),
                options,
                OptionMode.OPTIONAL,
                OptionValueMode.NONE);
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(VersionCommand.help().getText().isEmpty());
    }

    @Test
    void runShortShowsVersionOnly() {
        OutputContract output = command(route("short")).run();

        assertEquals(1, output.getMessages().size());
        assertEquals("1.2.3", output.getMessages().get(0).getText());
    }

    @Test
    void runPlainShowsVersionAndRuntime() {
        OutputContract output = command(route("plain")).run();

        assertTrue(
                output.getMessages().stream().anyMatch(m -> m.getText().contains("MyApp v1.2.3")));
    }

    /** Both options are declared, so the banner must come from neither being spelled. */
    @Test
    void runWithoutASpelledOptionShowsHeader() {
        OutputContract output = command(route()).run();

        assertEquals(1, output.getMessages().size());
        assertTrue(output.getMessages().get(0).getText().contains("MyApp v1.2.3"));
    }

    /** A route that declares no option falls through to the banner instead of throwing. */
    @Test
    void runWithARouteThatDeclaresNoOptionsShowsHeader() {
        RouteContract route = new Route("version", "Show version", (c, m) -> new Output());

        OutputContract output = command(route).run();

        assertEquals(1, output.getMessages().size());
        assertTrue(output.getMessages().get(0).getText().contains("MyApp v1.2.3"));
    }
}
