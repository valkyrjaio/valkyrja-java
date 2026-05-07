/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.middleware.handler;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ExitedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.ExitedHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class ExitedHandler extends Handler implements ExitedHandlerContract {

    public ExitedHandler(ContainerContract container) {
        super(container);
    }

    @Override
    public void exited(InputContract input, OutputContract output) {
        Class<?> next = this.next;
        if (next != null) {
            ((ExitedMiddlewareContract) getMiddleware(next)).exited(input, output, this);
        }
    }
}
