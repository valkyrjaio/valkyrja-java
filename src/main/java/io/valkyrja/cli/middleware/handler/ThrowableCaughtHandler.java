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
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class ThrowableCaughtHandler extends Handler<ThrowableCaughtMiddlewareContract>
        implements ThrowableCaughtHandlerContract {

    @SafeVarargs
    public ThrowableCaughtHandler(
            ContainerContract container,
            Class<? extends ThrowableCaughtMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public OutputContract throwableCaught(
            InputContract input, @Nullable OutputContract output, Throwable throwable) {
        Class<? extends ThrowableCaughtMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).throwableCaught(input, output, throwable, this)
                : Objects.requireNonNull(
                        output, "No middleware produced an output for the throwable");
    }
}
