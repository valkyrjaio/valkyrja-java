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
import io.valkyrja.http.message.response.TextResponse;
import org.junit.jupiter.api.Test;

/** Test the {@link TextResponse}. */
final class TextResponseTest {

    @Test
    void setsPlainContentTypeAndBody() {
        var response = new TextResponse("hi", StatusCode.OK, new HeaderCollection());

        assertEquals("hi", response.getBody().toString());
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("text/plain"));
    }

    @Test
    void defaultConstructorInjectsContentType() {
        assertTrue(new TextResponse().getHeaders().has("content-type"));
    }

    @Test
    void createAppliesDefaultsForNullArguments() {
        var defaults = TextResponse.create(null, null, null);

        assertEquals("", defaults.getBody().toString());
        assertEquals(StatusCode.OK, defaults.getStatusCode());

        var explicit = TextResponse.create("body", StatusCode.ACCEPTED, new HeaderCollection());

        assertEquals("body", explicit.getBody().toString());
        assertEquals(StatusCode.ACCEPTED, explicit.getStatusCode());
    }
}
