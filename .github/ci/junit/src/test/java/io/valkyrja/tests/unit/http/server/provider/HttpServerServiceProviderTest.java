/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.middleware.provider.HttpMiddlewareServiceProvider;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import io.valkyrja.http.server.middleware.CacheResponseMiddleware;
import io.valkyrja.http.server.middleware.routematched.RequestStructMiddleware;
import io.valkyrja.http.server.middleware.routematched.ResponseStructMiddleware;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.http.server.provider.HttpServerServiceProvider;
import io.valkyrja.log.logger.contract.LoggerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpServerServiceProvider}. */
final class HttpServerServiceProviderTest {

    @Test
    void publishersExposesFiveBindings() {
        assertEquals(5, new HttpServerServiceProvider().publishers().size());
    }

    @Test
    void publishMethodsBindEachService() {
        var container = new Container();
        container.setSingleton(ApplicationContract.class, mock(ApplicationContract.class));
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, mock(ThrowableCaughtHandlerContract.class));
        container.setSingleton(RouterContract.class, mock(RouterContract.class));
        container.setSingleton(
                RequestReceivedHandlerContract.class, mock(RequestReceivedHandlerContract.class));
        container.setSingleton(
                SendingResponseHandlerContract.class, mock(SendingResponseHandlerContract.class));
        container.setSingleton(
                ResponseSentHandlerContract.class, mock(ResponseSentHandlerContract.class));
        container.setSingleton(LoggerContract.class, mock(LoggerContract.class));

        HttpServerServiceProvider.publishLogThrowableCaughtMiddleware(container);
        HttpServerServiceProvider.publishRequestStructMiddleware(container);
        HttpServerServiceProvider.publishResponseStructMiddleware(container);
        HttpServerServiceProvider.publishCacheResponseMiddleware(container);
        HttpServerServiceProvider.publishRequestHandler(container);

        assertNotNull(container.getSingleton(RequestHandlerContract.class));
        assertNotNull(container.getSingleton(LogThrowableCaughtMiddleware.class));
        assertNotNull(container.getSingleton(RequestStructMiddleware.class));
        assertNotNull(container.getSingleton(ResponseStructMiddleware.class));
        assertNotNull(container.getSingleton(CacheResponseMiddleware.class));
    }

    @Test
    void publishRequestHandlerRunsEachConfiguredThrowableCaughtMiddlewareOnce() {
        var logger = mock(LoggerContract.class);
        var container = containerFor(new HttpConfig(), logger);

        var throwable = new IllegalStateException("boom");
        container
                .getSingleton(ThrowableCaughtHandlerContract.class)
                .throwableCaught(request(), new EmptyResponse(), throwable);

        verify(logger, times(1)).throwable(eq(throwable), any(String.class), any());
    }

    @Test
    void publishRequestHandlerAddsNoThrowableCaughtMiddlewareWhenTheConfigIsEmpty() {
        var logger = mock(LoggerContract.class);
        var config = mock(HttpConfigContract.class);
        when(config.throwableCaughtMiddleware()).thenReturn(List.of());
        var container = containerFor(config, logger);

        var response = new EmptyResponse();
        var actual =
                container
                        .getSingleton(ThrowableCaughtHandlerContract.class)
                        .throwableCaught(request(), response, new IllegalStateException("boom"));

        assertSame(response, actual);
        verify(logger, never()).throwable(any(), any(String.class), any());
    }

    /** Boot the throwable caught stage the way the providers do at run time. */
    private static Container containerFor(HttpConfigContract config, LoggerContract logger) {
        var container = new Container();
        container.setSingleton(ApplicationContract.class, mock(ApplicationContract.class));
        container.setSingleton(HttpConfigContract.class, config);
        container.setSingleton(LoggerContract.class, logger);
        container.setSingleton(RouterContract.class, mock(RouterContract.class));
        container.setSingleton(
                RequestReceivedHandlerContract.class, mock(RequestReceivedHandlerContract.class));
        container.setSingleton(
                SendingResponseHandlerContract.class, mock(SendingResponseHandlerContract.class));
        container.setSingleton(
                ResponseSentHandlerContract.class, mock(ResponseSentHandlerContract.class));

        HttpMiddlewareServiceProvider.publishThrowableCaughtHandler(container);
        HttpServerServiceProvider.publishLogThrowableCaughtMiddleware(container);
        HttpServerServiceProvider.publishRequestHandler(container);

        return container;
    }

    private static ServerRequestContract request() {
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri("/path"));

        return request;
    }
}
