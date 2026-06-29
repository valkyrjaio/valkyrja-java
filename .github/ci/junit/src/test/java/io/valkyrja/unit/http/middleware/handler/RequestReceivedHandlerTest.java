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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.fixtures.http.middleware.PassThroughHttpMiddleware;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
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
                PassThroughHttpMiddleware.class, new PassThroughHttpMiddleware());
        var request = mock(ServerRequestContract.class);

        var result =
                new RequestReceivedHandler(container, PassThroughHttpMiddleware.class)
                        .requestReceived(request);

        assertSame(request, result.request());
    }
}
