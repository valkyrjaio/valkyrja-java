/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.server.middleware.throwablecaught;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.server.middleware.throwablecaught.OutputThrowableCaughtMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link OutputThrowableCaughtMiddleware}. */
final class OutputThrowableCaughtMiddlewareTest {

    @Test
    void buildsErrorReportForThrowable() {
        var handler = mock(ThrowableCaughtHandlerContract.class);
        when(handler.throwableCaught(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));
        var middleware = new OutputThrowableCaughtMiddleware();
        var input = new Input().withCommandName("run");
        var throwable = new RuntimeException("boom");

        // Null output falls back to a fresh Output.
        assertNotNull(middleware.throwableCaught(input, null, throwable, handler));
        // A provided output is reused.
        assertNotNull(middleware.throwableCaught(input, new Output(), throwable, handler));
    }
}
