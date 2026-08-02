/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.middleware.provider.HttpMiddlewareServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMiddlewareServiceProvider}. */
final class HttpMiddlewareServiceProviderTest {

    private Container containerWithConfig() {
        var container = new Container();
        container.setSingleton(HttpConfigContract.class, mock(HttpConfigContract.class));

        return container;
    }

    @Test
    void publishersExposesAllSevenHandlers() {
        assertEquals(7, new HttpMiddlewareServiceProvider().publishers().size());
    }

    @Test
    void publishMethodsBindEachHandler() {
        var container = containerWithConfig();

        HttpMiddlewareServiceProvider.publishRequestReceivedHandler(container);
        HttpMiddlewareServiceProvider.publishThrowableCaughtHandler(container);
        HttpMiddlewareServiceProvider.publishRouteMatchedHandler(container);
        HttpMiddlewareServiceProvider.publishRouteNotMatchedHandler(container);
        HttpMiddlewareServiceProvider.publishRouteDispatchedHandler(container);
        HttpMiddlewareServiceProvider.publishSendingResponseHandler(container);
        HttpMiddlewareServiceProvider.publishResponseSentHandler(container);

        assertNotNull(container.getSingleton(RequestReceivedHandlerContract.class));
        assertNotNull(container.getSingleton(ThrowableCaughtHandlerContract.class));
        assertNotNull(container.getSingleton(RouteMatchedHandlerContract.class));
        assertNotNull(container.getSingleton(RouteNotMatchedHandlerContract.class));
        assertNotNull(container.getSingleton(RouteDispatchedHandlerContract.class));
        assertNotNull(container.getSingleton(SendingResponseHandlerContract.class));
        assertNotNull(container.getSingleton(ResponseSentHandlerContract.class));
    }
}
