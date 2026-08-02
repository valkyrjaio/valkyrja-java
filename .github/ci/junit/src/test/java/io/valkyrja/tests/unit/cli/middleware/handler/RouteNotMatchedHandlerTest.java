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
import io.valkyrja.cli.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteNotMatchedHandler}. */
final class RouteNotMatchedHandlerTest {

    @Test
    void returnsOutputWithoutMiddleware() {
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new RouteNotMatchedHandler(new Container())
                        .routeNotMatched(mock(InputContract.class), output));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new RouteNotMatchedHandler(container, PassThroughMiddlewareFixture.class)
                        .routeNotMatched(mock(InputContract.class), output));
    }
}
