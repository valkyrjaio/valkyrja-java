/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.middleware.inputreceived;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.server.middleware.inputreceived.CheckForHelpOptionsMiddleware;
import io.valkyrja.cli.server.middleware.inputreceived.CheckForVersionOptionsMiddleware;
import io.valkyrja.cli.server.middleware.inputreceived.CheckGlobalInteractionOptionsMiddleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli server input-received middleware. */
final class InputReceivedMiddlewareTest {

    private InputReceivedHandlerContract handler;

    @BeforeEach
    void setUp() {
        handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static Input withOption(String name) {
        return (Input) new Input().withAddedOption(new Option(name, OptionType.SHORT));
    }

    @Test
    void helpMiddlewareRewritesToHelpCommandWhenFlagPresent() {
        var middleware = new CheckForHelpOptionsMiddleware("help", "help", "h");

        var result = (InputContract) middleware.inputReceived(withOption("h"), handler);

        assertEquals("help", result.getCommandName());
        assertTrue(result.hasOption("command"));
    }

    @Test
    void helpMiddlewarePassesThroughWhenFlagAbsent() {
        var middleware = new CheckForHelpOptionsMiddleware("help", "help", "h");

        var result = (InputContract) middleware.inputReceived(new Input(), handler);

        assertEquals("list", result.getCommandName());
    }

    @Test
    void versionMiddlewareRewritesToVersionCommandAndClearsOptions() {
        var middleware = new CheckForVersionOptionsMiddleware("version", "version", "V");

        var result = (InputContract) middleware.inputReceived(withOption("V"), handler);

        assertEquals("version", result.getCommandName());
        assertTrue(result.getOptions().isEmpty());
    }

    @Test
    void versionMiddlewarePassesThroughWhenFlagAbsent() {
        var middleware = new CheckForVersionOptionsMiddleware("version", "version", "V");

        var result = (InputContract) middleware.inputReceived(new Input(), handler);

        assertEquals("list", result.getCommandName());
    }

    @Test
    void globalInteractionMiddlewareTogglesConfigFlags() {
        var config = new CliInteractionConfig();
        var middleware =
                new CheckGlobalInteractionOptionsMiddleware(
                        config, "no-interaction", "n", "quiet", "q", "silent", "s");

        var input =
                new Input()
                        .withAddedOption(new Option("n", OptionType.SHORT))
                        .withAddedOption(new Option("q", OptionType.SHORT))
                        .withAddedOption(new Option("s", OptionType.SHORT));
        middleware.inputReceived(input, handler);

        assertFalse(config.isInteractive());
        assertTrue(config.isQuiet());
        assertTrue(config.isSilent());
    }

    @Test
    void globalInteractionMiddlewareLeavesDefaultsWhenNoFlags() {
        var config = new CliInteractionConfig();
        var middleware =
                new CheckGlobalInteractionOptionsMiddleware(
                        config, "no-interaction", "n", "quiet", "q", "silent", "s");

        middleware.inputReceived(new Input(), handler);

        assertTrue(config.isInteractive());
        assertFalse(config.isQuiet());
        assertFalse(config.isSilent());
    }
}
