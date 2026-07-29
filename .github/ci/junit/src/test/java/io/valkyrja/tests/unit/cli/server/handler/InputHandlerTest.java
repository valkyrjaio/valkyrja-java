/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.server.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.InputHandler;
import io.valkyrja.cli.server.support.Exiter;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli server {@link InputHandler}. */
final class InputHandlerTest {

    private Container container;
    private RouterContract router;
    private InputReceivedHandlerContract inputReceivedHandler;
    private ThrowableCaughtHandlerContract throwableCaughtHandler;
    private ProcessExitingHandlerContract processExitingHandler;
    private final Input input = new Input();

    @BeforeEach
    void setUp() {
        container = new Container();
        router = mock(RouterContract.class);
        inputReceivedHandler = mock(InputReceivedHandlerContract.class);
        throwableCaughtHandler = mock(ThrowableCaughtHandlerContract.class);
        processExitingHandler = mock(ProcessExitingHandlerContract.class);
    }

    @AfterEach
    void unfreeze() {
        Exiter.unfreeze();
    }

    private InputHandler handler() {
        return new InputHandler(
                container,
                router,
                inputReceivedHandler,
                throwableCaughtHandler,
                processExitingHandler,
                mock(CliInteractionConfigContract.class));
    }

    @Test
    void handleDispatchesThroughRouter() {
        var output = new Output();
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(output);

        assertSame(output, handler().handle(input));
        assertSame(output, container.getSingleton(OutputContract.class));
    }

    @Test
    void handleReturnsEarlyOutputFromInputReceivedMiddleware() {
        var early = new EmptyOutput();
        when(inputReceivedHandler.inputReceived(any())).thenReturn(early);

        assertSame(early, handler().handle(input));
    }

    @Test
    void handleCatchesThrowableAndDelegatesToThrowableHandler() {
        var recovered = new Output();
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenThrow(new IllegalStateException("boom"));
        when(throwableCaughtHandler.throwableCaught(any(), isNull(), any())).thenReturn(recovered);

        assertSame(recovered, handler().handle(input));
    }

    @Test
    void runHandlesWritesExitsWithEnumExitCode() {
        Exiter.freeze();
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(new Output());

        assertDoesNotThrow(() -> handler().run(input));
        verify(processExitingHandler).processExiting(any(), any());
    }

    @Test
    void runWithIntegerExitCode() {
        Exiter.freeze();
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(new Output().withExitCode(5));

        assertDoesNotThrow(() -> handler().run(input));
    }

    @Test
    void exitDelegatesToProcessExitingHandler() {
        var output = new Output();

        handler().exit(input, output);

        verify(processExitingHandler).processExiting(input, output);
    }
}
