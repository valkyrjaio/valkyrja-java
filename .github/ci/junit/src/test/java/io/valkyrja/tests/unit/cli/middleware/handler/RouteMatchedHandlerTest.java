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
import io.valkyrja.cli.middleware.handler.RouteMatchedHandler;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteMatchedHandler}. */
final class RouteMatchedHandlerTest {

    @Test
    void returnsRouteWithoutMiddleware() {
        var route = mock(RouteContract.class);

        assertSame(
                route,
                new RouteMatchedHandler(new Container())
                        .routeMatched(mock(InputContract.class), route));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
        var route = mock(RouteContract.class);

        assertSame(
                route,
                new RouteMatchedHandler(container, PassThroughMiddlewareFixture.class)
                        .routeMatched(mock(InputContract.class), route));
    }
}
