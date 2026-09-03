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
                // The build of the first report, the dispatch, or the recovery write failed.
                // The write fails on any output that holds the destination that just failed.
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
                // The exit stage above runs a middleware, and the command's code still reaches
                // the shell, so a report is the only trace its failure leaves.
                getOutputFromThrowable(input, exitThrowable).writeMessages();
            } catch (Throwable recoveryThrowable) {
                // getRecoveryOutput raises nothing: it takes no override, its own try guards
                // the one call that reads the input, and every message reads through
                // messageOf. It writes through a plain Output, whose PrintStream raises none.
                getRecoveryOutput(input, exitThrowable, recoveryThrowable).writeMessages();
            }
        }

        Exiter.exit(exitCodeOf(input, output));
    }

    /**
     * Read the code an output ends the process with.
     *
     * <p>An output supplies this value, and a contract implementation can raise on the read or hold
     * a value the cast refuses. The code must reach the shell either way.
     *
     * @param input the input the command ran with
     * @param output the output the run ends with
     * @return the code the output holds, or the error code when the read raises and when the value
     *     fits no code
     */
    private int exitCodeOf(InputContract input, OutputContract output) {
        try {
            Object exitCode = output.getExitCode();
            return exitCode instanceof ExitCode ec ? ec.value : (int) exitCode;
        } catch (Throwable codeThrowable) {
            // This read runs last, so the report is the only trace the failure leaves.
            getRecoveryOutput(input, codeThrowable, null).writeMessages();

            return ExitCode.ERROR.value;
        }
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
     * Build the messages that report one throwable without reading the input.
     *
     * @param throwable the throwable to report
     * @return the messages that report the throwable
     */
    private MessageContract[] getBareThrowableMessages(Throwable throwable) {
        return new MessageContract[] {
            new ErrorMessage("Cli Server Error:"),
            new NewLine(),
            new ErrorMessage("Message:"),
            new Message(" " + messageOf(throwable)),
            new NewLine()
        };
    }

    /**
     * Read the message a throwable carries.
     *
     * <p>{@code getMessage} is overridable, so a throwable can raise on the call that reports it. A
     * report must not raise, and every report this class builds reads a message through here.
     *
     * @param throwable the throwable to read
     * @return the message the throwable carries, or a stand-in when it carries none or when reading
     *     it raises
     */
    private static String messageOf(Throwable throwable) {
        String message;

        try {
            message = throwable.getMessage();
        } catch (Throwable ignored) {
            message = null;
        }

        return message == null ? "the throwable reports no message" : message;
    }

    private static MessageContract[] concat(MessageContract[] first, MessageContract[] second) {
        MessageContract[] all = new MessageContract[first.length + second.length];
        System.arraycopy(first, 0, all, 0, first.length);
        System.arraycopy(second, 0, all, first.length, second.length);
        return all;
    }

    /**
     * Build the first report of a throwable, which a subclass overrides to reach a destination of
     * its own.
     *
     * @param input the input the command ran with
     * @param throwable the throwable a write or the exit stage raised
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
    private MessageContract[] getThrowableMessages(InputContract input, Throwable throwable) {
        return new MessageContract[] {
            new ErrorMessage("Cli Server Error:"),
            new NewLine(),
            new Message(input.getCommandName() + ": " + messageOf(throwable)),
            new NewLine()
        };
    }

    /**
     * Build the messages that report the throwable that ended a first report.
     *
     * @param recoveryThrowable the throwable that ended a first report
     * @return the messages that report the recovery throwable
     */
    private MessageContract[] getRecoveryMessages(Throwable recoveryThrowable) {
        return new MessageContract[] {
            new ErrorMessage("Recovery message:"),
            new Message(" " + messageOf(recoveryThrowable)),
            new NewLine()
        };
    }

    /**
     * Build a direct report, which names a throwable and the throwable that ended the attempt to
     * answer it. It names the one throwable where no attempt ran, and a third when building its own
     * full report raises.
     *
     * <p>A first report goes through {@code getOutputFromThrowable}, which a subclass overrides and
     * can point at any destination. This report answers an attempt that already failed, so it
     * builds an {@link Output} itself. No override and no flag reaches it.
     *
     * @param input the input the command ran with
     * @param throwable the throwable this handler caught
     * @param recoveryThrowable the throwable that ended the attempt to answer it, or null where no
     *     attempt ran
     * @return the output that reports the throwables
     */
    private OutputContract getRecoveryOutput(
            InputContract input, Throwable throwable, @Nullable Throwable recoveryThrowable) {
        MessageContract[] recoveryMessages =
                recoveryThrowable == null
                        ? new MessageContract[0]
                        : getRecoveryMessages(recoveryThrowable);
        MessageContract[] messages;

        try {
            messages = concat(getThrowableMessages(input, throwable), recoveryMessages);
        } catch (Throwable reportThrowable) {
            // The full report reads the command name from the input, so an input that raises
            // there takes the report with it. Naming that raise tells the reader why this
            // report holds no command.
            messages =
                    concat(
                            concat(getBareThrowableMessages(throwable), recoveryMessages),
                            new MessageContract[] {
                                new ErrorMessage("Report message:"),
                                new Message(" " + messageOf(reportThrowable)),
                                new NewLine()
                            });
        }

        return new Output().withExitCode(ExitCode.ERROR).withMessages(messages);
    }
}
