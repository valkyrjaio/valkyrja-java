/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.middleware.inputreceived;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.server.middleware.inputreceived.CheckForHelpOptionsMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link CheckForHelpOptionsMiddleware}. */
final class CheckForHelpOptionsMiddlewareTest {

    @Test
    void rewritesToHelpCommandWhenOptionPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var middleware = new CheckForHelpOptionsMiddleware("help", "help", "h");

        assertNotNull(
                middleware.inputReceived(
                        new Input().withOptions(new Option("h", OptionType.SHORT)), handler));
        // Without the option the input passes through untouched.
        assertNotNull(middleware.inputReceived(new Input(), handler));
    }

    @Test
    void rewritesWhenLongOptionPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertNotNull(
                new CheckForHelpOptionsMiddleware("help", "help", "h")
                        .inputReceived(
                                new Input().withOptions(new Option("help", OptionType.LONG)),
                                handler));
    }
}
