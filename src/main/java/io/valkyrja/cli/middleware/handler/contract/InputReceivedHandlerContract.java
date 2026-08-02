/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.handler.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;

public interface InputReceivedHandlerContract
        extends HandlerContract<InputReceivedMiddlewareContract> {

    /** Returns InputContract to continue, or OutputContract to short-circuit. */
    Object inputReceived(InputContract input);
}
