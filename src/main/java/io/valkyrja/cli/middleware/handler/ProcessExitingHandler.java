/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
