/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.server.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.throwable.exception.HttpResponseException;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the http server {@link RequestHandler}. */
final class RequestHandlerTest {

    private Container container;
    private RouterContract router;
    private RequestReceivedHandlerContract requestReceivedHandler;
    private ThrowableCaughtHandlerContract throwableCaughtHandler;
    private SendingResponseHandlerContract sendingResponseHandler;
    private ResponseSentHandlerContract responseSentHandler;
    private final ServerRequestContract request = mock(ServerRequestContract.class);

    @BeforeEach
    void setUp() {
        container = new Container();
        router = mock(RouterContract.class);
        requestReceivedHandler = mock(RequestReceivedHandlerContract.class);
        throwableCaughtHandler = mock(ThrowableCaughtHandlerContract.class);
        sendingResponseHandler = mock(SendingResponseHandlerContract.class);
        responseSentHandler = mock(ResponseSentHandlerContract.class);
        when(requestReceivedHandler.requestReceived(any()))
                .thenAnswer(inv -> new RequestReceivedResult(inv.getArgument(0), null));
        when(throwableCaughtHandler.throwableCaught(any(), any(), any()))
                .thenAnswer(inv -> inv.getArgument(1));
        when(sendingResponseHandler.sendingResponse(any(), any()))
                .thenAnswer(inv -> inv.getArgument(1));
    }

    private RequestHandler handler(boolean debug) {
        return new RequestHandler(
                container,
                router,
                requestReceivedHandler,
                throwableCaughtHandler,
                sendingResponseHandler,
                responseSentHandler,
                debug);
    }

    @Test
    void noArgConstructorIsUsable() {
        assertNotNull(new RequestHandler());
    }

    @Test
    void handleDispatchesViaRouter() {
        var response = new EmptyResponse();
        when(router.dispatch(any())).thenReturn(response);

        assertSame(response, handler(false).handle(request));
        assertSame(response, container.getSingleton(ResponseContract.class));
    }

    @Test
    void handleReturnsEarlyMiddlewareResponse() {
        var early = new EmptyResponse();
        when(requestReceivedHandler.requestReceived(any()))
                .thenReturn(new RequestReceivedResult(request, early));

        assertSame(early, handler(false).handle(request));
    }

    @Test
    void handleWrapsUnknownThrowableInServerError() {
        when(router.dispatch(any())).thenThrow(new IllegalStateException("boom"));

        var response = handler(false).handle(request);

        assertEquals(StatusCode.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleUsesResponseFromHttpResponseException() {
        var carried = new EmptyResponse();
        when(router.dispatch(any()))
                .thenThrow(
                        new HttpResponseException(
                                StatusCode.NOT_FOUND, "nf", new HeaderCollection(), carried));

        var response = handler(false).handle(request);

        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleBuildsDefaultResponseForHttpExceptionWithoutResponse() {
        when(router.dispatch(any()))
                .thenThrow(
                        new HttpResponseException(
                                StatusCode.NOT_FOUND, "nf", new HeaderCollection(), null));

        var response = handler(false).handle(request);

        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void debugModeRethrows() {
        when(router.dispatch(any())).thenThrow(new IllegalStateException("boom"));

        assertThrows(RuntimeException.class, () -> handler(true).handle(request));
    }

    @Test
    void sendTerminateAndRun() {
        when(router.dispatch(any())).thenReturn(new EmptyResponse());

        var handler = handler(false);
        assertDoesNotThrow(() -> handler.send(new EmptyResponse()));
        handler.terminate(request, new EmptyResponse());
        verify(responseSentHandler).responseSent(any(), any());

        assertDoesNotThrow(() -> handler.run(request));
    }
}
