/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.fixtures.cli.middleware.PassThroughMiddleware;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.ExitedHandler;
import io.valkyrja.cli.middleware.handler.InputReceivedHandler;
import io.valkyrja.cli.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteMatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.cli.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli middleware handler chain and the abstract {@code Handler} base. */
final class MiddlewareHandlerTest {

    private Container container;
    private final Input input = new Input();
    private final OutputContract output = new EmptyOutput();
    private final RouteContract route = new Route("list", "List", (c, r) -> new EmptyOutput());

    @BeforeEach
    void setUp() {
        container = new Container();
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());
    }

    @Test
    void inputReceivedReturnsInputWhenNoMiddleware() {
        assertSame(input, new InputReceivedHandler(container).inputReceived(input));
    }

    @Test
    void inputReceivedRunsThroughMiddleware() {
        var handler = new InputReceivedHandler(container, PassThroughMiddleware.class);

        assertSame(input, handler.inputReceived(input));
    }

    @Test
    void addAppendsMiddleware() {
        var handler = new InputReceivedHandler(container);
        handler.add(PassThroughMiddleware.class);

        assertSame(input, handler.inputReceived(input));
    }

    @Test
    void routeMatchedReturnsRouteWhenNoMiddleware() {
        assertSame(route, new RouteMatchedHandler(container).routeMatched(input, route));
    }

    @Test
    void routeMatchedRunsThroughMiddleware() {
        var handler = new RouteMatchedHandler(container, PassThroughMiddleware.class);

        assertSame(route, handler.routeMatched(input, route));
    }

    @Test
    void routeNotMatchedReturnsOutput() {
        assertSame(output, new RouteNotMatchedHandler(container).routeNotMatched(input, output));
        assertSame(
                output,
                new RouteNotMatchedHandler(container, PassThroughMiddleware.class)
                        .routeNotMatched(input, output));
    }

    @Test
    void routeDispatchedReturnsOutput() {
        assertSame(
                output,
                new RouteDispatchedHandler(container).routeDispatched(input, output, route));
        assertSame(
                output,
                new RouteDispatchedHandler(container, PassThroughMiddleware.class)
                        .routeDispatched(input, output, route));
    }

    @Test
    void throwableCaughtReturnsOutputWhenPresent() {
        var throwable = new IllegalStateException("boom");

        assertSame(
                output,
                new ThrowableCaughtHandler(container).throwableCaught(input, output, throwable));
        assertSame(
                output,
                new ThrowableCaughtHandler(container, PassThroughMiddleware.class)
                        .throwableCaught(input, output, throwable));
    }

    @Test
    void throwableCaughtThrowsWhenNoOutputAndNoMiddleware() {
        assertThrows(
                NullPointerException.class,
                () ->
                        new ThrowableCaughtHandler(container)
                                .throwableCaught(input, null, new IllegalStateException("boom")));
    }

    @Test
    void exitedRunsWithAndWithoutMiddleware() {
        assertDoesNotThrow(() -> new ExitedHandler(container).exited(input, output));
        assertDoesNotThrow(
                () ->
                        new ExitedHandler(container, PassThroughMiddleware.class)
                                .exited(input, output));
    }
}
