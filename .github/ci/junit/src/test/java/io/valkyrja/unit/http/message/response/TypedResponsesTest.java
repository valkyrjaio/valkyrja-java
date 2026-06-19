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
import io.valkyrja.http.message.response.HtmlResponse;
import io.valkyrja.http.message.response.TextResponse;
import io.valkyrja.http.message.response.XmlResponse;
import org.junit.jupiter.api.Test;

/** Test the simple typed {@link io.valkyrja.http.message.response.Response} subclasses. */
final class TypedResponsesTest {

    @Test
    void htmlResponseSetsHtmlContentType() {
        var response = new HtmlResponse("<p>hi</p>", StatusCode.OK, new HeaderCollection());

        assertEquals("<p>hi</p>", response.getBody().toString());
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("text/html"));
    }

    @Test
    void htmlResponseDefault() {
        assertTrue(new HtmlResponse().getHeaders().has("content-type"));
    }

    @Test
    void textResponseSetsPlainContentType() {
        var response = TextResponse.create("hi", null, null);

        assertEquals("hi", response.getBody().toString());
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("text/plain"));
        assertTrue(new TextResponse().getHeaders().has("content-type"));
    }

    @Test
    void xmlResponseSetsXmlContentType() {
        var response = new XmlResponse("<x/>", StatusCode.OK, new HeaderCollection());

        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("xml"));
        assertTrue(new XmlResponse().getHeaders().has("content-type"));
    }

    @Test
    void emptyResponseHasNoContentStatus() {
        var response = new EmptyResponse();

        assertEquals(StatusCode.NO_CONTENT, response.getStatusCode());
        assertTrue(new EmptyResponse(new HeaderCollection()).getBody().toString().isEmpty());
    }
}
