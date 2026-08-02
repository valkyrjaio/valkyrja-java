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
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link ThrowableCaughtHandler}. */
final class ThrowableCaughtHandlerTest {

    @Test
    void returnsOutputWithoutMiddleware() {
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new ThrowableCaughtHandler(new Container())
                        .throwableCaught(
                                mock(InputContract.class), output, new RuntimeException("x")));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new ThrowableCaughtHandler(container, PassThroughMiddlewareFixture.class)
                        .throwableCaught(
                                mock(InputContract.class), output, new RuntimeException("x")));
    }
}
