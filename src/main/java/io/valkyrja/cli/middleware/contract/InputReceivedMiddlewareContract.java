/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;

public interface InputReceivedMiddlewareContract {

    /** Returns either an InputContract to continue routing, or OutputContract to short-circuit. */
    Object inputReceived(InputContract input, InputReceivedHandlerContract handler);
}
