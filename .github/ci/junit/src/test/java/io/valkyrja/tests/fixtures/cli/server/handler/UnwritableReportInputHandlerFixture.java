/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.server.handler;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.FileOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.InputHandler;
import io.valkyrja.container.manager.contract.ContainerContract;

/** Testable InputHandler whose throwable report writes to a filepath that takes no write. */
public final class UnwritableReportInputHandlerFixture extends InputHandler {

    private final String filepath;

    public UnwritableReportInputHandlerFixture(
            ContainerContract container,
            RouterContract router,
            InputReceivedHandlerContract inputReceivedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            ProcessExitingHandlerContract processExitingHandler,
            CliInteractionConfigContract interactionConfig,
            String filepath) {
        super(
                container,
                router,
                inputReceivedHandler,
                throwableCaughtHandler,
                processExitingHandler,
                interactionConfig);
        this.filepath = filepath;
    }

    @Override
    protected OutputContract getOutputFromThrowable(InputContract input, Throwable throwable) {
        return new FileOutput(filepath).withAddedMessage(new Message(throwable.getMessage()));
    }
}
