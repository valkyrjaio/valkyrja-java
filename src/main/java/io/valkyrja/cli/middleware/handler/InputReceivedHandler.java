/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
