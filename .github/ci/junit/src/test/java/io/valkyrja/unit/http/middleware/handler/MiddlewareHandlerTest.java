/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.fixtures.http.middleware.PassThroughHttpMiddleware;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.http.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.http.middleware.handler.RouteMatchedHandler;
import io.valkyrja.http.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.TerminatedHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
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
        container.setSingleton(PassThroughHttpMiddleware.class, new PassThroughHttpMiddleware());
    }

    @Test
    void requestReceivedChain() {
        assertNotNull(new RequestReceivedHandler(container).requestReceived(request));
        var handler = new RequestReceivedHandler(container, PassThroughHttpMiddleware.class);
        handler.add(PassThroughHttpMiddleware.class);
        assertNotNull(handler.requestReceived(request));
    }

    @Test
    void routeMatchedChain() {
        assertNotNull(new RouteMatchedHandler(container).routeMatched(request, route));
        assertNotNull(
                new RouteMatchedHandler(container, PassThroughHttpMiddleware.class)
                        .routeMatched(request, route));
    }

    @Test
    void routeNotMatchedChain() {
        assertSame(response, new RouteNotMatchedHandler(container).routeNotMatched(request, response));
        assertSame(
                response,
                new RouteNotMatchedHandler(container, PassThroughHttpMiddleware.class)
                        .routeNotMatched(request, response));
    }

    @Test
    void routeDispatchedChain() {
        assertSame(
                response,
                new RouteDispatchedHandler(container).routeDispatched(request, response, route));
        assertSame(
                response,
                new RouteDispatchedHandler(container, PassThroughHttpMiddleware.class)
                        .routeDispatched(request, response, route));
    }

    @Test
    void sendingResponseChain() {
        assertSame(
                response, new SendingResponseHandler(container).sendingResponse(request, response));
        assertSame(
                response,
                new SendingResponseHandler(container, PassThroughHttpMiddleware.class)
                        .sendingResponse(request, response));
    }

    @Test
    void throwableCaughtChain() {
        var throwable = new IllegalStateException("boom");
        assertSame(
                response,
                new ThrowableCaughtHandler(container).throwableCaught(request, response, throwable));
        assertSame(
                response,
                new ThrowableCaughtHandler(container, PassThroughHttpMiddleware.class)
                        .throwableCaught(request, response, throwable));
    }

    @Test
    void terminatedChain() {
        assertDoesNotThrow(() -> new TerminatedHandler(container).terminated(request, response));
        assertDoesNotThrow(
                () ->
                        new TerminatedHandler(container, PassThroughHttpMiddleware.class)
                                .terminated(request, response));
    }
}
