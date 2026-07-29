/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.routing.factory;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.routing.factory.RoutingResponseFactory;
import io.valkyrja.http.routing.url.contract.UrlContract;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link RoutingResponseFactory}. */
final class RoutingResponseFactoryTest {

    @Test
    void createRouteRedirectResponseResolvesUrlAndDelegates() {
        var responseFactory = mock(ResponseFactoryContract.class);
        var url = mock(UrlContract.class);
        var redirect = mock(RedirectResponseContract.class);
        when(url.getUrl(eq("users.show"), any())).thenReturn("/users/42");
        when(responseFactory.createRedirectResponse(eq("/users/42"), any(), any()))
                .thenReturn(redirect);

        var factory = new RoutingResponseFactory(responseFactory, url);
        var result =
                factory.createRouteRedirectResponse(
                        "users.show", Map.of("id", 42), StatusCode.FOUND, new HeaderCollection());

        assertSame(redirect, result);
    }
}
