/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.middleware.throwablecaught;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jspecify.annotations.Nullable;

public class LogThrowableCaughtMiddleware implements ThrowableCaughtMiddlewareContract {

    protected Logger logger;

    public LogThrowableCaughtMiddleware(Logger logger) {
        this.logger = logger;
    }

    @Override
    public OutputContract throwableCaught(
            InputContract input,
            @Nullable OutputContract output,
            Throwable throwable,
            ThrowableCaughtHandlerContract handler) {
        String commandName = input.getCommandName();
        String logMessage = "Cli Server Error\nUrl: " + commandName;
        logger.log(Level.SEVERE, logMessage, throwable);
        return handler.throwableCaught(input, output, throwable);
    }
}
