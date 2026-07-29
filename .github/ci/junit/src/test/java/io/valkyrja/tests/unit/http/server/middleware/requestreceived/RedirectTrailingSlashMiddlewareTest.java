/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.server.middleware.requestreceived;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.server.middleware.requestreceived.RedirectTrailingSlashMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link RedirectTrailingSlashMiddleware}. */
final class RedirectTrailingSlashMiddlewareTest {

    private final RedirectTrailingSlashMiddleware middleware =
            new RedirectTrailingSlashMiddleware();

    private static RequestReceivedHandlerContract passThroughHandler() {
        var handler = mock(RequestReceivedHandlerContract.class);
        when(handler.requestReceived(any()))
                .thenAnswer(inv -> new RequestReceivedResult(inv.getArgument(0), null));
        return handler;
    }

    private static ServerRequestContract requestWithPath(String path) {
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri(path));
        return request;
    }

    @Test
    void redirectsWhenPathHasTrailingSlash() {
        var result = middleware.requestReceived(requestWithPath("/users/"), passThroughHandler());

        assertNotNull(result.response());
    }

    @Test
    void passesThroughWhenNoTrailingSlash() {
        var result = middleware.requestReceived(requestWithPath("/users"), passThroughHandler());

        assertNull(result.response());
    }

    @Test
    void doesNotRedirectRoot() {
        var result = middleware.requestReceived(requestWithPath("/"), passThroughHandler());

        assertNull(result.response());
    }
}
