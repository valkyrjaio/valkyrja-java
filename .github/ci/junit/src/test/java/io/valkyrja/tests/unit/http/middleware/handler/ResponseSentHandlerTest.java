/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.ResponseSentHandler;
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link ResponseSentHandler}. */
final class ResponseSentHandlerTest {

    @Test
    void runsWithAndWithoutMiddleware() {
        var container = new Container();
        container.setSingleton(
                PassThroughHttpMiddlewareFixture.class, new PassThroughHttpMiddlewareFixture());

        assertDoesNotThrow(
                () ->
                        new ResponseSentHandler(new Container())
                                .responseSent(
                                        mock(ServerRequestContract.class),
                                        mock(ResponseContract.class)));
        assertDoesNotThrow(
                () ->
                        new ResponseSentHandler(container, PassThroughHttpMiddlewareFixture.class)
                                .responseSent(
                                        mock(ServerRequestContract.class),
                                        mock(ResponseContract.class)));
    }
}
