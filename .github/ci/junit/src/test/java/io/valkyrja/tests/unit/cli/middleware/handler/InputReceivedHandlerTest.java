/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.handler.InputReceivedHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link InputReceivedHandler}. */
final class InputReceivedHandlerTest {

    @Test
    void returnsInputWithoutMiddleware() {
        var input = mock(InputContract.class);

        assertSame(input, new InputReceivedHandler(new Container()).inputReceived(input));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
        var input = mock(InputContract.class);

        assertSame(
                input,
                new InputReceivedHandler(container, PassThroughMiddlewareFixture.class)
                        .inputReceived(input));
    }
}
