/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.middleware.throwablecaught;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.cli.server.middleware.throwablecaught.OutputThrowableCaughtMiddleware;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli server throwable-caught middleware. */
final class ThrowableCaughtMiddlewareTest {

    private ThrowableCaughtHandlerContract handler;

    @BeforeEach
    void setUp() {
        handler = mock(ThrowableCaughtHandlerContract.class);
        when(handler.throwableCaught(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));
    }

    @Test
    void logMiddlewareLogsAndDelegates() {
        var logger = mock(Logger.class);
        var middleware = new LogThrowableCaughtMiddleware(logger);
        var throwable = new IllegalStateException("boom");
        var output = new Output();

        middleware.throwableCaught(new Input(), output, throwable, handler);

        verify(logger).log(eq(Level.SEVERE), any(String.class), eq(throwable));
        verify(handler).throwableCaught(any(), eq(output), eq(throwable));
    }

    @Test
    void outputMiddlewareRendersErrorDetails() {
        var middleware = new OutputThrowableCaughtMiddleware();
        var throwable = new IllegalStateException("kaboom");

        OutputContract result =
                middleware.throwableCaught(new Input(), new Output(), throwable, handler);

        assertEquals(ExitCode.ERROR, result.getExitCode());
        assertTrue(result.getMessages().stream().anyMatch(m -> m.getText().contains("kaboom")));
    }

    @Test
    void outputMiddlewareCreatesOutputWhenNoneProvided() {
        var middleware = new OutputThrowableCaughtMiddleware();
        var throwable = new IllegalStateException("kaboom");

        OutputContract result =
                middleware.throwableCaught(new Input(), null, throwable, handler);

        assertEquals(ExitCode.ERROR, result.getExitCode());
    }
}