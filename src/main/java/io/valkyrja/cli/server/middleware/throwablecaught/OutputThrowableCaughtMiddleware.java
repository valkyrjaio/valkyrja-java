/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.middleware.throwablecaught;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.message.Banner;
import io.valkyrja.cli.interaction.message.ErrorMessage;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.io.PrintWriter;
import java.io.StringWriter;

public class OutputThrowableCaughtMiddleware implements ThrowableCaughtMiddlewareContract {

    @Override
    public OutputContract throwableCaught(InputContract input, OutputContract output, Throwable throwable, ThrowableCaughtHandlerContract handler) {
        String commandName = input.getCommandName();

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));

        output = output
            .withExitCode(ExitCode.ERROR)
            .withMessages(
                new Banner(new ErrorMessage("Cli Server Error:")),
                new NewLine(),
                new ErrorMessage("Command:"),
                new Message(" " + commandName),
                new NewLine(),
                new NewLine(),
                new ErrorMessage("Message:"),
                new Message(" " + throwable.getMessage()),
                new NewLine(),
                new NewLine(),
                new ErrorMessage("Line:"),
                new Message(" " + throwable.getStackTrace()[0].getLineNumber()),
                new NewLine(),
                new NewLine(),
                new ErrorMessage("Trace:"),
                new NewLine(),
                new Message(sw.toString() + "\n")
            );

        return handler.throwableCaught(input, output, throwable);
    }
}
