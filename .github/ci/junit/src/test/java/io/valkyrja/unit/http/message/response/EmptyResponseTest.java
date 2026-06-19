/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.EmptyResponse;
import org.junit.jupiter.api.Test;

/** Test the {@link EmptyResponse}. */
final class EmptyResponseTest {

    @Test
    void defaultConstructorIsNoContentWithEmptyBody() {
        var response = new EmptyResponse();

        assertEquals(StatusCode.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().toString().isEmpty());
    }

    @Test
    void headerConstructorKeepsNoContentStatus() {
        var response = new EmptyResponse(new HeaderCollection());

        assertEquals(StatusCode.NO_CONTENT, response.getStatusCode());
    }
}
