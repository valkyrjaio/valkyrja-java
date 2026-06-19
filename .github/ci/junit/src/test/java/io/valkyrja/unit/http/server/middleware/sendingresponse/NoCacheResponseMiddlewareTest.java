/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.server.middleware.sendingresponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.server.middleware.sendingresponse.NoCacheResponseMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link NoCacheResponseMiddleware}. */
final class NoCacheResponseMiddlewareTest {

    @Test
    void addsNoCacheHeaders() {
        var middleware = new NoCacheResponseMiddleware();
        var handler = mock(SendingResponseHandlerContract.class);
        when(handler.sendingResponse(any(), any())).thenAnswer(inv -> inv.getArgument(1));

        ResponseContract result =
                middleware.sendingResponse(
                        mock(ServerRequestContract.class), new EmptyResponse(), handler);

        assertTrue(result.getHeaders().has("cache-control"));
        assertTrue(result.getHeaders().has("expires"));
        assertTrue(result.getHeaders().has("pragma"));
    }
}