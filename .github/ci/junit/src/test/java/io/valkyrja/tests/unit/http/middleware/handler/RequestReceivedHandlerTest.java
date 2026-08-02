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
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestReceivedHandler}. */
final class RequestReceivedHandlerTest {

    @Test
    void wrapsRequestWithoutMiddleware() {
        var request = mock(ServerRequestContract.class);

        var result = new RequestReceivedHandler(new Container()).requestReceived(request);

        assertSame(request, result.request());
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(
                PassThroughHttpMiddlewareFixture.class, new PassThroughHttpMiddlewareFixture());
        var request = mock(ServerRequestContract.class);

        var result =
                new RequestReceivedHandler(container, PassThroughHttpMiddlewareFixture.class)
                        .requestReceived(request);

        assertSame(request, result.request());
    }
}
