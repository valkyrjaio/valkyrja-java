/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.http.middleware.handler.ResponseSentHandler;
import io.valkyrja.http.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.http.middleware.handler.RouteMatchedHandler;
import io.valkyrja.http.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http middleware handler chain and the abstract {@code Handler} base. */
final class MiddlewareHandlerTest {

    private Container container;
    private final ServerRequestContract request = mock(ServerRequestContract.class);
    private final ResponseContract response = new EmptyResponse();
    private final RouteContract route =
            new Route("/x", "x", (container, route) -> new EmptyResponse());

    @BeforeEach
    void setUp() {
        container = new Container();
        container.setSingleton(
                PassThroughHttpMiddlewareFixture.class, new PassThroughHttpMiddlewareFixture());
    }

    @Test
    void requestReceivedChain() {
        assertNotNull(new RequestReceivedHandler(container).requestReceived(request));
        var handler = new RequestReceivedHandler(container, PassThroughHttpMiddlewareFixture.class);
        handler.add(PassThroughHttpMiddlewareFixture.class);
        assertNotNull(handler.requestReceived(request));
    }

    @Test
    void routeMatchedChain() {
        assertNotNull(new RouteMatchedHandler(container).routeMatched(request, route));
        assertNotNull(
                new RouteMatchedHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .routeMatched(request, route));
    }

    @Test
    void routeNotMatchedChain() {
        assertSame(
                response, new RouteNotMatchedHandler(container).routeNotMatched(request, response));
        assertSame(
                response,
                new RouteNotMatchedHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .routeNotMatched(request, response));
    }

    @Test
    void routeDispatchedChain() {
        assertSame(
                response,
                new RouteDispatchedHandler(container).routeDispatched(request, response, route));
        assertSame(
                response,
                new RouteDispatchedHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .routeDispatched(request, response, route));
    }

    @Test
    void sendingResponseChain() {
        assertSame(
                response, new SendingResponseHandler(container).sendingResponse(request, response));
        assertSame(
                response,
                new SendingResponseHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .sendingResponse(request, response));
    }

    @Test
    void throwableCaughtChain() {
        var throwable = new IllegalStateException("boom");
        assertSame(
                response,
                new ThrowableCaughtHandler(container)
                        .throwableCaught(request, response, throwable));
        assertSame(
                response,
                new ThrowableCaughtHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .throwableCaught(request, response, throwable));
    }

    @Test
    void responseSentChain() {
        assertDoesNotThrow(
                () -> new ResponseSentHandler(container).responseSent(request, response));
        assertDoesNotThrow(
                () ->
                        new ResponseSentHandler(container, PassThroughHttpMiddlewareFixture.class)
                                .responseSent(request, response));
    }
}
