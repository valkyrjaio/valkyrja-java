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

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestReceivedResult}. */
final class RequestReceivedResultTest {

    @Test
    void exposesRequestAndResponse() {
        var request = mock(ServerRequestContract.class);
        var response = new EmptyResponse();

        var result = new RequestReceivedResult(request, response);

        assertSame(request, result.request());
        assertSame(response, result.response());
        assertNull(new RequestReceivedResult(request, null).response());
    }
}
