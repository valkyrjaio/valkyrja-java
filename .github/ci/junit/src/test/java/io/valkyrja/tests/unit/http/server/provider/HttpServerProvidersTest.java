/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.http.middleware.handler.ResponseSentHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.dispatcher.Router;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import io.valkyrja.http.server.middleware.CacheResponseMiddleware;
import io.valkyrja.http.server.middleware.routematched.RequestStructMiddleware;
import io.valkyrja.http.server.middleware.routematched.ResponseStructMiddleware;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.http.server.provider.HttpServerComponentProvider;
import io.valkyrja.http.server.provider.HttpServerServiceProvider;
import io.valkyrja.log.logger.NullLogger;
import io.valkyrja.log.logger.contract.LoggerContract;
import org.junit.jupiter.api.Test;

/** Test the http server component and service providers. */
final class HttpServerProvidersTest {

    @Test
    void componentProvider() {
        var provider = new HttpServerComponentProvider();
        var app = mock(ApplicationContract.class);

        assertInstanceOf(
                HttpServerServiceProvider.class, provider.getContainerProviders(app).get(0));
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void publishersExposeRequestHandlerAndMiddleware() {
        assertEquals(5, new HttpServerServiceProvider().publishers().size());
    }

    @Test
    void publishMiddlewareServices() {
        var container = new Container();
        container.setSingleton(LoggerContract.class, new NullLogger());
        var app = mock(ApplicationContract.class);
        when(app.getDebugMode()).thenReturn(false);
        container.setSingleton(ApplicationContract.class, app);

        HttpServerServiceProvider.publishLogThrowableCaughtMiddleware(container);
        HttpServerServiceProvider.publishRequestStructMiddleware(container);
        HttpServerServiceProvider.publishResponseStructMiddleware(container);
        HttpServerServiceProvider.publishCacheResponseMiddleware(container);

        assertInstanceOf(
                LogThrowableCaughtMiddleware.class,
                container.getSingleton(LogThrowableCaughtMiddleware.class));
        assertInstanceOf(
                RequestStructMiddleware.class,
                container.getSingleton(RequestStructMiddleware.class));
        assertInstanceOf(
                ResponseStructMiddleware.class,
                container.getSingleton(ResponseStructMiddleware.class));
        assertInstanceOf(
                CacheResponseMiddleware.class,
                container.getSingleton(CacheResponseMiddleware.class));
    }

    @Test
    void publishRequestHandlerBindsHandler() {
        var container = new Container();
        var app = mock(ApplicationContract.class);
        when(app.getDebugMode()).thenReturn(true);
        container.setSingleton(ApplicationContract.class, app);
        container.setSingleton(RouterContract.class, new Router(container));
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, new ThrowableCaughtHandler(container));
        container.setSingleton(
                RequestReceivedHandlerContract.class, new RequestReceivedHandler(container));
        container.setSingleton(
                SendingResponseHandlerContract.class, new SendingResponseHandler(container));
        container.setSingleton(
                ResponseSentHandlerContract.class, new ResponseSentHandler(container));

        HttpServerServiceProvider.publishRequestHandler(container);

        assertInstanceOf(
                RequestHandlerContract.class, container.getSingleton(RequestHandlerContract.class));
    }
}
