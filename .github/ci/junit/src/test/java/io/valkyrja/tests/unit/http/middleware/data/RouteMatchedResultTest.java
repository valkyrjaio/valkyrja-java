/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.middleware.data;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.routing.data.contract.RouteContract;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteMatchedResult}. */
final class RouteMatchedResultTest {

    @Test
    void exposesRouteAndResponse() {
        var route = mock(RouteContract.class);
        var response = new EmptyResponse();

        var result = new RouteMatchedResult(route, response);

        assertSame(route, result.route());
        assertSame(response, result.response());
        assertNull(new RouteMatchedResult(route, null).response());
    }
}
