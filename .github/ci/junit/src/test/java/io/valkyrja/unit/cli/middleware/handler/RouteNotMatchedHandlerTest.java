/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.fixtures.cli.middleware.PassThroughMiddleware;
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
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());
        var output = mock(OutputContract.class);

        assertSame(
                output,
                new RouteNotMatchedHandler(container, PassThroughMiddleware.class)
                        .routeNotMatched(mock(InputContract.class), output));
    }
}
