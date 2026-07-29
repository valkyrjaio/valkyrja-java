/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.server.command.VersionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link VersionCommand}. */
final class VersionCommandTest {

    private OutputFactoryContract outputFactory;
    private RouteContract route;

    @BeforeEach
    void setUp() {
        outputFactory = mock(OutputFactoryContract.class);
        when(outputFactory.createOutput()).thenReturn(new Output());
        route = mock(RouteContract.class);
        lenient().when(route.getName()).thenReturn("version");
        lenient().when(route.getDescription()).thenReturn("Show version");
    }

    private VersionCommand command() {
        return new VersionCommand(outputFactory, "MyApp", "1.2.3", route);
    }

    @Test
    void helpReturnsMessage() {
        assertFalse(VersionCommand.help().getText().isEmpty());
    }

    @Test
    void runShortShowsVersionOnly() {
        when(route.hasOption("short")).thenReturn(true);

        var output = command().run();

        assertEquals(1, output.getMessages().size());
        assertEquals("1.2.3", output.getMessages().get(0).getText());
    }

    @Test
    void runPlainShowsVersionAndRuntime() {
        when(route.hasOption("short")).thenReturn(false);
        when(route.hasOption("plain")).thenReturn(true);

        var output = command().run();

        assertTrue(
                output.getMessages().stream().anyMatch(m -> m.getText().contains("MyApp v1.2.3")));
    }

    @Test
    void runDefaultShowsHeader() {
        when(route.hasOption("short")).thenReturn(false);
        when(route.hasOption("plain")).thenReturn(false);

        var output = command().run();

        assertEquals(1, output.getMessages().size());
        assertTrue(output.getMessages().get(0).getText().contains("MyApp v1.2.3"));
    }
}
