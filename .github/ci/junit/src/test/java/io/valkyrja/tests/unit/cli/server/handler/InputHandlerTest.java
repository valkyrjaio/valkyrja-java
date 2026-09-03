/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.server.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.FileOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionFileWriteException;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.InputHandler;
import io.valkyrja.cli.server.support.Exiter;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.interaction.input.RaisingCommandNameInputFixture;
import io.valkyrja.tests.fixtures.cli.interaction.output.RaisingExitCodeOutputFixture;
import io.valkyrja.tests.fixtures.cli.server.handler.RaisingMessageThrowableFixture;
import io.valkyrja.tests.fixtures.cli.server.handler.UnwritableReportInputHandlerFixture;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

/** Test the cli server {@link InputHandler}. */
final class InputHandlerTest {

    private static final String FILENAME = "out.txt";

    private Container container;
    private RouterContract router;
    private InputReceivedHandlerContract inputReceivedHandler;
    private ThrowableCaughtHandlerContract throwableCaughtHandler;
    private ProcessExitingHandlerContract processExitingHandler;
    private final Input input = new Input();
    private final PrintStream originalOut = System.out;

    @TempDir Path directory;

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
        System.setOut(originalOut);
        Exiter.unfreeze();
    }

    private String capture(Runnable runnable) {
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        runnable.run();
        System.setOut(originalOut);
        return buffer.toString(StandardCharsets.UTF_8);
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
        // A silent output records the message without writing it to stdout.
        when(router.dispatch(any()))
                .thenReturn(new Output().withIsSilent(true).withAddedMessage(new Message("hi")));

        assertDoesNotThrow(() -> handler().run(input));

        var exited = ArgumentCaptor.forClass(OutputContract.class);
        verify(processExitingHandler).processExiting(any(), exited.capture());
        assertTrue(exited.getValue().hasWrittenMessage());
        assertFalse(exited.getValue().hasUnwrittenMessage());
        assertSame(exited.getValue(), container.getSingleton(OutputContract.class));
    }

    @Test
    void runRoutesAWriteThrowableThroughTheThrowableCaughtHandler() {
        Exiter.freeze();

        var recovered = new Output();
        var unwritable =
                new FileOutput(directory.resolve("missing").resolve(FILENAME).toString())
                        .withAddedMessage(new Message("hello"));

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(unwritable);
        when(throwableCaughtHandler.throwableCaught(any(), any(), any())).thenReturn(recovered);

        assertDoesNotThrow(() -> handler().run(input));

        var reported = ArgumentCaptor.forClass(OutputContract.class);
        verify(throwableCaughtHandler)
                .throwableCaught(
                        any(), reported.capture(), any(CliInteractionFileWriteException.class));
        assertEquals(ExitCode.ERROR, reported.getValue().getExitCode());
        assertTrue(reported.getValue().hasUnwrittenMessage());
        verify(processExitingHandler).processExiting(any(), any());
    }

    @Test
    void runFallsBackToAPrintingOutputWhenTheRecoveryWriteAlsoFails() {
        Exiter.freeze();

        var unwritablePath = directory.resolve("missing").resolve(FILENAME).toString();
        var unwritable = new FileOutput(unwritablePath).withAddedMessage(new Message("hello"));
        var alsoUnwritable =
                new FileOutput(unwritablePath).withAddedMessage(new Message("recovery"));

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(unwritable);
        when(throwableCaughtHandler.throwableCaught(any(), any(), any()))
                .thenReturn(alsoUnwritable);

        assertDoesNotThrow(() -> handler().run(input));

        var exited = ArgumentCaptor.forClass(OutputContract.class);
        verify(processExitingHandler).processExiting(any(), exited.capture());
        assertEquals(ExitCode.ERROR, exited.getValue().getExitCode());
        assertTrue(exited.getValue().hasWrittenMessage());
        assertFalse(exited.getValue().hasUnwrittenMessage());
        assertTrue(
                exited.getValue().getWrittenMessages().stream()
                        .anyMatch(m -> m.getText().equals("Recovery message:")));
        assertSame(exited.getValue(), container.getSingleton(OutputContract.class));
    }

    @Test
    void runRecoversWhenTheThrowableCaughtMiddlewareItselfThrows() {
        Exiter.freeze();

        var unwritablePath = directory.resolve("missing").resolve(FILENAME).toString();
        var unwritable = new FileOutput(unwritablePath).withAddedMessage(new Message("hello"));

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenReturn(unwritable);
        when(throwableCaughtHandler.throwableCaught(any(), any(), any()))
                .thenThrow(new IllegalStateException("middleware"));

        assertDoesNotThrow(() -> handler().run(input));

        var exited = ArgumentCaptor.forClass(OutputContract.class);
        verify(processExitingHandler).processExiting(any(), exited.capture());
        assertEquals(ExitCode.ERROR, exited.getValue().getExitCode());
    }

    @Test
    void handleReportsBothThrowablesWhenTheThrowableCaughtMiddlewareThrows() {
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenThrow(new IllegalStateException("command"));
        when(throwableCaughtHandler.throwableCaught(any(), isNull(), any()))
                .thenThrow(new IllegalStateException("middleware"));

        var output = handler().handle(input);
        var printed = capture(output::writeMessages);

        assertEquals(ExitCode.ERROR, output.getExitCode());
        // The full report names the command, which the report that reads no input cannot.
        assertTrue(printed.contains("list: command"));
        assertTrue(printed.contains("Recovery message:"));
        assertTrue(printed.contains("middleware"));
    }

    @Test
    void runReportsAProcessExitingThrowableAndKeepsTheExitCode() {
        Exiter.freeze();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        // A silent output writes nothing, so only the report of the exit throwable prints.
        when(router.dispatch(any()))
                .thenReturn(new Output().withIsSilent(true).withExitCode(ExitCode.USAGE_ERROR));
        doThrow(new IllegalStateException("exiting"))
                .when(processExitingHandler)
                .processExiting(any(), any());

        var printed = capture(() -> assertDoesNotThrow(() -> handler().run(input)));

        // The report is the only trace the failure leaves.
        assertTrue(printed.contains("Cli Server Error:"));
        assertTrue(printed.contains("exiting"));
        // The report ends its own line, and the frozen exiter prints the code after it.
        assertTrue(printed.endsWith("\n" + ExitCode.USAGE_ERROR.value));
    }

    @Test
    void runSignalsTheExitCodeWhenTheExitStageReportAlsoFails() {
        Exiter.freeze();

        var unwritablePath = directory.resolve("missing").resolve(FILENAME).toString();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any()))
                .thenReturn(new Output().withIsSilent(true).withExitCode(ExitCode.USAGE_ERROR));
        doThrow(new IllegalStateException("exiting"))
                .when(processExitingHandler)
                .processExiting(any(), any());

        // An override of the report can hold a destination that takes no write.
        var handler =
                new UnwritableReportInputHandlerFixture(
                        container,
                        router,
                        inputReceivedHandler,
                        throwableCaughtHandler,
                        processExitingHandler,
                        mock(CliInteractionConfigContract.class),
                        unwritablePath);

        var printed = capture(() -> assertDoesNotThrow(() -> handler.run(input)));

        // The input reads, so the report that answers the failed one still names the command.
        assertTrue(printed.contains("list: exiting"));
        assertTrue(printed.contains("Recovery message:"));
        // The command's own code still reaches the shell.
        assertTrue(printed.endsWith("\n" + ExitCode.USAGE_ERROR.value));
    }

    @Test
    void runTakesTheReportThatReadsNoInputWhenTheFullReportRaises() {
        Exiter.freeze();

        var unwritablePath = directory.resolve("missing").resolve(FILENAME).toString();
        var unwritable = new FileOutput(unwritablePath).withAddedMessage(new Message("hello"));
        // The full report reads the command name, so every report that reads the input raises.
        var raisingInput = new RaisingCommandNameInputFixture();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(raisingInput);
        when(router.dispatch(any())).thenReturn(unwritable);

        var printed = capture(() -> assertDoesNotThrow(() -> handler().run(raisingInput)));

        // The write fails, the full report raises on the command name, and the report that
        // reads no input names both.
        assertTrue(printed.contains("Cli Server Error:"));
        assertTrue(printed.contains("Recovery message:"));
        assertTrue(printed.contains("input"));
    }

    @Test
    void runReportsAnExitStageThrowableWhoseMessageRaises() {
        Exiter.freeze();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any()))
                .thenReturn(new Output().withIsSilent(true).withExitCode(ExitCode.USAGE_ERROR));
        // The throwable's own message raises, so every report that names it reads the
        // stand-in rather than raising.
        doThrow(new RaisingMessageThrowableFixture())
                .when(processExitingHandler)
                .processExiting(any(), any());

        var printed = capture(() -> assertDoesNotThrow(() -> handler().run(input)));

        // messageOf stands in for the message, so the first report names the command and
        // prints rather than raising.
        assertTrue(printed.contains("list: the throwable reports no message"));
        // The command's own code still reaches the shell.
        assertTrue(printed.endsWith("\n" + ExitCode.USAGE_ERROR.value));
    }

    @Test
    void handleStandsInForARecoveryThrowableWhoseMessageRaises() {
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        when(router.dispatch(any())).thenThrow(new IllegalStateException("command"));
        // The middleware's throwable raises on its own message.
        when(throwableCaughtHandler.throwableCaught(any(), isNull(), any()))
                .thenThrow(new RaisingMessageThrowableFixture());

        var output = handler().handle(input);
        var printed = capture(output::writeMessages);

        // The full report still names the command, so getRecoveryMessages stood in rather
        // than raising into the report that reads no input.
        assertTrue(printed.contains("list: command"));
        assertTrue(printed.contains("Recovery message:"));
        assertTrue(printed.contains("the throwable reports no message"));
    }

    @Test
    void runStandsInForAnExitStageThrowableWhoseMessageRaisesWhenTheInputRaises() {
        Exiter.freeze();

        // Reading the input raises, so the run reaches the report that reads no input, and
        // that report names a throwable whose own message raises.
        var raisingInput = new RaisingCommandNameInputFixture();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(raisingInput);
        when(router.dispatch(any()))
                .thenReturn(new Output().withIsSilent(true).withExitCode(ExitCode.USAGE_ERROR));
        doThrow(new RaisingMessageThrowableFixture())
                .when(processExitingHandler)
                .processExiting(any(), any());

        var printed = capture(() -> assertDoesNotThrow(() -> handler().run(raisingInput)));

        assertTrue(printed.contains("the throwable reports no message"));
        assertTrue(printed.endsWith("\n" + ExitCode.USAGE_ERROR.value));
    }

    @Test
    void handleStandsInForTheRecoveryThrowableWhenTheInputRaisesToo() {
        // Reading the input raises, so the report that reads no input takes over, and the
        // throwable it names raises on its own message.
        var raisingInput = new RaisingCommandNameInputFixture();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(raisingInput);
        when(router.dispatch(any())).thenThrow(new IllegalStateException("command"));
        when(throwableCaughtHandler.throwableCaught(any(), isNull(), any()))
                .thenThrow(new RaisingMessageThrowableFixture());

        var output = handler().handle(raisingInput);
        var printed = capture(output::writeMessages);

        assertTrue(printed.contains("command"));
        assertTrue(printed.contains("the throwable reports no message"));
        // The report reads no input, so it names no command, and it names the raise that
        // removed the command from it.
        assertFalse(printed.contains("list:"));
        assertTrue(printed.contains("Report message:"));
        assertTrue(printed.contains("input"));
    }

    @Test
    void handleStandsInForAThrowableThatCarriesNoMessage() {
        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        // getMessage returns null rather than raising, which is the other half of the stand-in.
        when(router.dispatch(any())).thenThrow(new IllegalStateException());
        when(throwableCaughtHandler.throwableCaught(any(), isNull(), any()))
                .thenThrow(new IllegalStateException("middleware"));

        var output = handler().handle(input);
        var printed = capture(output::writeMessages);

        assertTrue(printed.contains("list: the throwable reports no message"));
        assertFalse(printed.contains("list: null"));
    }

    @Test
    void runExitsWithTheErrorCodeWhenTheOutputRaisesOnItsCode() {
        Exiter.freeze();

        when(inputReceivedHandler.inputReceived(any())).thenReturn(input);
        // An output supplies the code, and this one raises on the read.
        when(router.dispatch(any())).thenReturn(new RaisingExitCodeOutputFixture());

        var printed = capture(() -> assertDoesNotThrow(() -> handler().run(input)));

        // The guard names what it swallowed, and the input reads, so it names the command.
        assertTrue(printed.contains("list: exit code"));
        // No attempt preceded this read, so the report carries no recovery line.
        assertFalse(printed.contains("Recovery message:"));
        assertTrue(printed.endsWith("\n" + ExitCode.ERROR.value));
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
