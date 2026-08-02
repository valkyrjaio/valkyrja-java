/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link SendingResponseHandler}. */
final class SendingResponseHandlerTest {

    @Test
    void returnsResponseWithoutMiddleware() {
        var response = mock(ResponseContract.class);

        assertSame(
                response,
                new SendingResponseHandler(new Container())
                        .sendingResponse(mock(ServerRequestContract.class), response));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughHttpMiddlewareFixture.class, new PassThroughHttpMiddlewareFixture());
        var response = mock(ResponseContract.class);

        assertSame(
                response,
                new SendingResponseHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .sendingResponse(mock(ServerRequestContract.class), response));
    }
}
