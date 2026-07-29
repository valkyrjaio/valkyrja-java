/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteDispatchedHandler}. */
final class RouteDispatchedHandlerTest {

    @Test
    void returnsOutputWithoutMiddleware() {
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new RouteDispatchedHandler(new Container())
                        .routeDispatched(
                                mock(InputContract.class), output, mock(RouteContract.class)));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new RouteDispatchedHandler(container, PassThroughMiddleware.class)
                        .routeDispatched(
                                mock(InputContract.class), output, mock(RouteContract.class)));
    }
}
