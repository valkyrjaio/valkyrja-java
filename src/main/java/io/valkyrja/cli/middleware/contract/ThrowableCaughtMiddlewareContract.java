/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import org.jspecify.annotations.Nullable;

public interface ThrowableCaughtMiddlewareContract {

    OutputContract throwableCaught(
            InputContract input,
            @Nullable OutputContract output,
            Throwable throwable,
            ThrowableCaughtHandlerContract handler);
}
