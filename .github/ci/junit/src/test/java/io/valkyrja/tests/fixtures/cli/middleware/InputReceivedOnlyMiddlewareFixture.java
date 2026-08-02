/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.middleware;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;

/** Implements only the input-received contract, none of the route-matched/dispatched/etc. ones. */
public final class InputReceivedOnlyMiddlewareFixture implements InputReceivedMiddlewareContract {

    @Override
    public Object inputReceived(InputContract input, InputReceivedHandlerContract handler) {
        return handler.inputReceived(input);
    }
}
