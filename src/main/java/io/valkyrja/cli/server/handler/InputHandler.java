/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.handler;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.message.ErrorMessage;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.contract.InputHandlerContract;
import io.valkyrja.cli.server.support.Exiter;
import io.valkyrja.container.manager.contract.ContainerContract;
import org.jspecify.annotations.Nullable;

public class InputHandler implements InputHandlerContract {

    protected final ContainerContract container;
    protected final RouterContract router;
    protected final InputReceivedHandlerContract inputReceivedHandler;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final ProcessExitingHandlerContract processExitingHandler;
    protected final CliInteractionConfigContract interactionConfig;

    public InputHandler(
            ContainerContract container,
            RouterContract router,
            InputReceivedHandlerContract inputReceivedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            ProcessExitingHandlerContract processExitingHandler,
            CliInteractionConfigContract interactionConfig) {
        this.container = container;
        this.router = router;
        this.inputReceivedHandler = inputReceivedHandler;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.processExitingHandler = processExitingHandler;
        this.interactionConfig = interactionConfig;
    }

    @Override
    public OutputContract handle(InputContract input) {
        OutputContract output;
        try {
            output = dispatchRouter(input);
        } catch (Throwable throwable) {
            try {
                // A middleware runs here, so the dispatch belongs under a guard of its own.
                output = throwableCaughtHandler.throwableCaught(input, emptyOutput(), throwable);
            } catch (Throwable recoveryThrowable) {
                output = getRecoveryOutput(input, throwable, recoveryThrowable);
            }
        }
        container.setSingleton(OutputContract.class, output);
        return output;
    }

    @Override
    public void exit(InputContract input, OutputContract output) {
        processExitingHandler.processExiting(input, output);
    }

    @Override
    public void run(InputContract input) {
        OutputContract output = handle(input);

        try {
            output = output.writeMessages();
        } catch (Throwable throwable) {
            try {
                // A middleware runs here, so the dispatch belongs under the same guard as the
                // write.
                output =
                        throwableCaughtHandler.throwableCaught(
                                input, getOutputFromThrowable(input, throwable), throwable);
                output = output.writeMessages();
            } catch (Throwable recoveryThrowable) {
                // The dispatch or the recovery write failed. A middleware can throw, or it can
                // return an output whose destination is the one that failed.
                output = getRecoveryOutput(input, throwable, recoveryThrowable);
                output = output.writeMessages();
            }
        } finally {
            container.setSingleton(OutputContract.class, output);
        }

        try {
            exit(input, output);
        } catch (Throwable exitThrowable) {
            try {
                // A middleware runs here, and the command's code still reaches the shell, so
                // this report is the only trace the failure leaves.
                getOutputFromThrowable(input, exitThrowable).writeMessages();
            } catch (Throwable reportThrowable) {
                try {
                    getRecoveryOutput(input, exitThrowable, reportThrowable).writeMessages();
                } catch (Throwable ignored) {
                    // The report is the last write, so a failure here leaves no trace to write.
                }
            }
        }

        Object exitCode = output.getExitCode();
        int code = exitCode instanceof ExitCode ec ? ec.value : (int) exitCode;
        Exiter.exit(code);
    }

    protected OutputContract dispatchRouter(InputContract input) {
        container.setSingleton(InputContract.class, input);
        Object afterMiddleware = inputReceivedHandler.inputReceived(input);
        if (afterMiddleware instanceof OutputContract earlyOutput) {
            return earlyOutput;
        }
        InputContract processedInput = (InputContract) afterMiddleware;
        container.setSingleton(InputContract.class, processedInput);
        return router.dispatch(processedInput);
    }

    protected @Nullable OutputContract emptyOutput() {
        return null;
    }

    /**
     * Build the messages that report two throwables without reading the input.
     *
     * @param throwable the throwable the write raised
     * @param recoveryThrowable the throwable the recovery raised
     * @return the messages that report both throwables
     */
    protected MessageContract[] getFallbackThrowableMessages(
            Throwable throwable, Throwable recoveryThrowable) {
        // This report answers a report that raised, so it repeats no call of it. It spells the
        // recovery lines out rather than calling getRecoveryMessages, which the caller's try
        // already ran.
        return new MessageContract[] {
            new ErrorMessage("Cli Server Error:"),
            new NewLine(),
            new ErrorMessage("Message:"),
            new Message(" " + throwable.getMessage()),
            new NewLine(),
            new ErrorMessage("Recovery message:"),
            new Message(" " + recoveryThrowable.getMessage()),
            new NewLine()
        };
    }

    private static MessageContract[] concat(MessageContract[] first, MessageContract[] second) {
        MessageContract[] all = new MessageContract[first.length + second.length];
        System.arraycopy(first, 0, all, 0, first.length);
        System.arraycopy(second, 0, all, first.length, second.length);
        return all;
    }

    /**
     * Build the output that reports a throwable the configured destination could not carry.
     *
     * @param input the input the command ran with
     * @param throwable the throwable the write raised
     * @return the output that reports the throwable
     */
    protected OutputContract getOutputFromThrowable(InputContract input, Throwable throwable) {
        // OutputThrowableCaughtMiddleware builds the component's full error report. This is the
        // minimal fallback that prints when no middleware replaces these messages.
        return new Output()
                .withExitCode(ExitCode.ERROR)
                .withMessages(getThrowableMessages(input, throwable));
    }

    /**
     * Build the messages that report a throwable.
     *
     * @param input the input the command ran with
     * @param throwable the throwable to report
     * @return the messages that report the throwable
     */
    protected MessageContract[] getThrowableMessages(InputContract input, Throwable throwable) {
        return new MessageContract[] {
            new ErrorMessage("Cli Server Error:"),
            new NewLine(),
            new Message(input.getCommandName() + ": " + throwable.getMessage()),
            new NewLine()
        };
    }

    /**
     * Build the messages that report the throwable a recovery raised.
     *
     * @param recoveryThrowable the throwable the recovery raised
     * @return the messages that report the recovery throwable
     */
    protected MessageContract[] getRecoveryMessages(Throwable recoveryThrowable) {
        return new MessageContract[] {
            new ErrorMessage("Recovery message:"),
            new Message(" " + recoveryThrowable.getMessage()),
            new NewLine()
        };
    }

    /**
     * Build the output that reports a throwable and the throwable a recovery raised.
     *
     * <p>A first report goes through {@code getOutputFromThrowable}, which a subclass overrides and
     * can point at any destination. This report answers a report that already failed, so it builds
     * a plain {@link Output} itself. No override redirects it to the destination that just failed,
     * which is the failure this report answers.
     *
     * @param input the input the command ran with
     * @param throwable the throwable the write raised
     * @param recoveryThrowable the throwable the recovery raised
     * @return the output that reports both throwables
     */
    private OutputContract getRecoveryOutput(
            InputContract input, Throwable throwable, Throwable recoveryThrowable) {
        MessageContract[] messages;

        try {
            messages =
                    concat(
                            getThrowableMessages(input, throwable),
                            getRecoveryMessages(recoveryThrowable));
        } catch (Throwable reportThrowable) {
            // The full report reads the command name from the input, so an input that raises
            // there takes the report with it.
            messages = getFallbackThrowableMessages(throwable, recoveryThrowable);
        }

        return new Output().withExitCode(ExitCode.ERROR).withMessages(messages);
    }
}
