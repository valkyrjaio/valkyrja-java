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
            output = throwableCaughtHandler.throwableCaught(input, emptyOutput(), throwable);
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
            output.writeMessages();
        } catch (Throwable throwable) {
            output =
                    throwableCaughtHandler.throwableCaught(
                            input, getOutputFromThrowable(input, throwable), throwable);
            container.setSingleton(OutputContract.class, output);

            try {
                output.writeMessages();
            } catch (Throwable recoveryThrowable) {
                // A middleware can return an output whose destination is the one that failed. This
                // last resort leads with the throwable the command's own destination raised, and it
                // names both failures.
                output =
                        getOutputFromThrowable(input, throwable)
                                .withAddedMessages(
                                        new NewLine(),
                                        new ErrorMessage("Recovery message:"),
                                        new Message(" " + recoveryThrowable.getMessage()));
                container.setSingleton(OutputContract.class, output);
                output.writeMessages();
            }
        }

        exit(input, output);
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
                .withMessages(
                        new ErrorMessage("Cli Server Error:"),
                        new NewLine(),
                        new Message(input.getCommandName() + ": " + throwable.getMessage()));
    }
}
