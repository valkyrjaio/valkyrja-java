/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.middleware.throwablecaught;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogThrowableCaughtMiddleware implements ThrowableCaughtMiddlewareContract {

    protected Logger logger;

    public LogThrowableCaughtMiddleware(Logger logger) {
        this.logger = logger;
    }

    @Override
    public OutputContract throwableCaught(InputContract input, OutputContract output, Throwable throwable, ThrowableCaughtHandlerContract handler) {
        String commandName = input.getCommandName();
        String logMessage = "Cli Server Error\nUrl: " + commandName;
        logger.log(Level.SEVERE, logMessage, throwable);
        return handler.throwableCaught(input, output, throwable);
    }
}
