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
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class InputReceivedHandler extends Handler implements InputReceivedHandlerContract {

    public InputReceivedHandler(ContainerContract container) {
        super(container);
    }

    @Override
    public Object inputReceived(InputContract input) {
        Class<?> next = this.next;
        return next != null
                ? ((InputReceivedMiddlewareContract) getMiddleware(next)).inputReceived(input, this)
                : input;
    }
}
