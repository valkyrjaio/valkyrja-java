/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.middleware.throwablecaught;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.log.logger.contract.LoggerContract;
import org.junit.jupiter.api.Test;

/** Test the http server {@link LogThrowableCaughtMiddleware}. */
final class LogThrowableCaughtMiddlewareTest {

    @Test
    void logsThrowableAndDelegates() {
        var logger = mock(LoggerContract.class);
        var middleware = new LogThrowableCaughtMiddleware(logger);
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri("/path"));
        var response = new EmptyResponse();
        var throwable = new IllegalStateException("boom");
        var handler = mock(ThrowableCaughtHandlerContract.class);
        when(handler.throwableCaught(any(), any(), any())).thenReturn(response);

        middleware.throwableCaught(request, response, throwable, handler);

        verify(logger).throwable(eq(throwable), any(String.class), any());
        verify(handler).throwableCaught(request, response, throwable);
    }
}
