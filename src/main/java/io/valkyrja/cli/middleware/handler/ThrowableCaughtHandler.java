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
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class ThrowableCaughtHandler extends Handler implements ThrowableCaughtHandlerContract {

    public ThrowableCaughtHandler(ContainerContract container) {
        super(container);
    }

    @Override
    public OutputContract throwableCaught(InputContract input, OutputContract output, Throwable throwable) {
        Class<?> next = this.next;
        return next != null
                ? ((ThrowableCaughtMiddlewareContract) getMiddleware(next))
                        .throwableCaught(input, output, throwable, this)
                : output;
    }
}
