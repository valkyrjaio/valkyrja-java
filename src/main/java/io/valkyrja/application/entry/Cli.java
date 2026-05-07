/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.application.entry.abstract_.App;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.input.factory.InputFactory;
import io.valkyrja.cli.server.handler.contract.InputHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class Cli extends App {

    public static void run(CliConfigContract config, String[] args) {

        ApplicationContract app = start(config);
        ContainerContract container = app.getContainer();

        InputHandlerContract handler = container.getSingleton(InputHandlerContract.class);

        InputContract input = getInput(config, args);

        handler.run(input);
    }

    protected static InputContract getInput(CliConfigContract config, String[] args) {
        return InputFactory.fromGlobals(
                args, config.applicationName(), config.defaultCommandName());
    }
}
