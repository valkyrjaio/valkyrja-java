/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.Container;
import io.valkyrja.fixtures.http.middleware.PassThroughHttpMiddleware;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
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
        container.setSingleton(PassThroughHttpMiddleware.class, new PassThroughHttpMiddleware());
        var response = mock(ResponseContract.class);

        assertSame(
                response,
                new SendingResponseHandler(container, PassThroughHttpMiddleware.class)
                        .sendingResponse(mock(ServerRequestContract.class), response));
    }
}
