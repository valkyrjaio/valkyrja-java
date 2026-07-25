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
import io.valkyrja.cli.middleware.contract.ProcessExitingMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class ProcessExitingHandler extends Handler<ProcessExitingMiddlewareContract>
        implements ProcessExitingHandlerContract {

    @SafeVarargs
    public ProcessExitingHandler(
            ContainerContract container,
            Class<? extends ProcessExitingMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public void processExiting(InputContract input, OutputContract output) {
        Class<? extends ProcessExitingMiddlewareContract> next = this.next;
        if (next != null) {
            getMiddleware(next).processExiting(input, output, this);
        }
    }
}
