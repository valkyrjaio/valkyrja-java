/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.HtmlResponse;
import org.junit.jupiter.api.Test;

/** Test the {@link HtmlResponse}. */
final class HtmlResponseTest {

    @Test
    void setsHtmlContentTypeAndBody() {
        var response = new HtmlResponse("<p>hi</p>", StatusCode.OK, new HeaderCollection());

        assertEquals("<p>hi</p>", response.getBody().toString());
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("text/html"));
    }

    @Test
    void defaultConstructorInjectsContentType() {
        assertTrue(new HtmlResponse().getHeaders().has("content-type"));
    }
}
