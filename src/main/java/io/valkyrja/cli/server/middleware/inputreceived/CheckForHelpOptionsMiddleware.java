/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.middleware.inputreceived;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;

public class CheckForHelpOptionsMiddleware implements InputReceivedMiddlewareContract {

    protected String commandName;
    protected String optionName;
    protected String optionShortName;

    public CheckForHelpOptionsMiddleware(
            String commandName, String optionName, String optionShortName) {
        this.commandName = commandName;
        this.optionName = optionName;
        this.optionShortName = optionShortName;
    }

    @Override
    public Object inputReceived(InputContract input, InputReceivedHandlerContract handler) {
        if (input.hasOption(optionShortName) || input.hasOption(optionName)) {
            input =
                    input.withCommandName(commandName)
                            .withOptions(
                                    new Option("command", input.getCommandName(), OptionType.LONG));
        }
        return handler.inputReceived(input);
    }
}
