/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.middleware.inputreceived;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.server.middleware.inputreceived.CheckGlobalInteractionOptionsMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link CheckGlobalInteractionOptionsMiddleware}. */
final class CheckGlobalInteractionOptionsMiddlewareTest {

    private static CheckGlobalInteractionOptionsMiddleware middleware(CliInteractionConfig config) {
        return new CheckGlobalInteractionOptionsMiddleware(
                config, "no-interaction", "n", "quiet", "q", "silent", "s");
    }

    @Test
    void appliesInteractionFlagsWhenOptionsPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var config = new CliInteractionConfig();
        var input =
                new Input()
                        .withOptions(new Option("n", OptionType.SHORT))
                        .withAddedOption(new Option("q", OptionType.SHORT))
                        .withAddedOption(new Option("s", OptionType.SHORT));

        middleware(config).inputReceived(input, handler);

        assertFalse(config.isInteractive());
        assertTrue(config.isQuiet());
        assertTrue(config.isSilent());
    }

    @Test
    void leavesDefaultsWhenOptionsAbsent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var config = new CliInteractionConfig();

        middleware(config).inputReceived(new Input(), handler);

        assertTrue(config.isInteractive());
    }

    @Test
    void appliesFlagsWhenLongOptionsPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var config = new CliInteractionConfig();
        var input =
                new Input()
                        .withOptions(new Option("no-interaction", OptionType.LONG))
                        .withAddedOption(new Option("quiet", OptionType.LONG))
                        .withAddedOption(new Option("silent", OptionType.LONG));

        middleware(config).inputReceived(input, handler);

        assertFalse(config.isInteractive());
        assertTrue(config.isQuiet());
        assertTrue(config.isSilent());
    }

}
