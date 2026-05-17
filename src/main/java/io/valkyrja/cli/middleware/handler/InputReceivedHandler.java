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

public class InputReceivedHandler extends Handler<InputReceivedMiddlewareContract>
        implements InputReceivedHandlerContract {

    @SafeVarargs
    public InputReceivedHandler(
            ContainerContract container,
            Class<? extends InputReceivedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public Object inputReceived(InputContract input) {
        Class<? extends InputReceivedMiddlewareContract> next = this.next;
        return next != null ? getMiddleware(next).inputReceived(input, this) : input;
    }
}
