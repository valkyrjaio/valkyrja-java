/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.InputReceivedHandler;
import io.valkyrja.cli.middleware.handler.ProcessExitingHandler;
import io.valkyrja.cli.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteMatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.cli.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
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
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
    }

    @Test
    void inputReceivedReturnsInputWhenNoMiddleware() {
        assertSame(input, new InputReceivedHandler(container).inputReceived(input));
    }

    @Test
    void inputReceivedRunsThroughMiddleware() {
        var handler = new InputReceivedHandler(container, PassThroughMiddlewareFixture.class);

        assertSame(input, handler.inputReceived(input));
    }

    @Test
    void addAppendsMiddleware() {
        var handler = new InputReceivedHandler(container);
        handler.add(PassThroughMiddlewareFixture.class);

        assertSame(input, handler.inputReceived(input));
    }

    @Test
    void routeMatchedReturnsRouteWhenNoMiddleware() {
        assertSame(route, new RouteMatchedHandler(container).routeMatched(input, route));
    }

    @Test
    void routeMatchedRunsThroughMiddleware() {
        var handler = new RouteMatchedHandler(container, PassThroughMiddlewareFixture.class);

        assertSame(route, handler.routeMatched(input, route));
    }

    @Test
    void routeNotMatchedReturnsOutput() {
        assertSame(output, new RouteNotMatchedHandler(container).routeNotMatched(input, output));
        assertSame(
                output,
                new RouteNotMatchedHandler(container, PassThroughMiddlewareFixture.class)
                        .routeNotMatched(input, output));
    }

    @Test
    void routeDispatchedReturnsOutput() {
        assertSame(
                output,
                new RouteDispatchedHandler(container).routeDispatched(input, output, route));
        assertSame(
                output,
                new RouteDispatchedHandler(container, PassThroughMiddlewareFixture.class)
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
                new ThrowableCaughtHandler(container, PassThroughMiddlewareFixture.class)
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
    void processExitingRunsWithAndWithoutMiddleware() {
        assertDoesNotThrow(
                () -> new ProcessExitingHandler(container).processExiting(input, output));
        assertDoesNotThrow(
                () ->
                        new ProcessExitingHandler(container, PassThroughMiddlewareFixture.class)
                                .processExiting(input, output));
    }

    /** A developer binds a middleware as a service, and the handler resolves it. */
    @Test
    void resolvesAMiddlewareBoundAsAService() {
        var serviceContainer = new Container();
        serviceContainer.bind(
                PassThroughMiddlewareFixture.class, (c, a) -> new PassThroughMiddlewareFixture());

        var handler =
                new InputReceivedHandler(serviceContainer, PassThroughMiddlewareFixture.class);

        assertNotNull(handler.inputReceived(input));
    }

    /** A developer binds a middleware as an alias, and the handler resolves it. */
    @Test
    void resolvesAMiddlewareBoundAsAnAlias() {
        var aliasContainer = new Container();
        aliasContainer.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());
        aliasContainer.bindAlias(
                raw(InputReceivedMiddlewareContract.class),
                raw(PassThroughMiddlewareFixture.class));

        var handler =
                new InputReceivedHandler(aliasContainer, InputReceivedMiddlewareContract.class);

        assertNotNull(handler.inputReceived(input));
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }
}
