/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.ContentTypeValue;
import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.uri.Uri;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link JsonServerRequest}. */
final class JsonServerRequestTest {

    private static JsonServerRequest withJsonBody(String body) {
        var stream = new Stream();
        stream.write(body);
        stream.rewind();
        var headers =
                new HeaderCollection(
                        new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.APPLICATION_JSON));
        return new JsonServerRequest(
                new Uri(),
                RequestMethod.POST,
                stream,
                headers,
                ProtocolVersion.V1_1,
                new ServerParamCollection(Map.of()),
                new CookieParamCollection(Map.of()),
                new QueryParamCollection(Map.of()),
                new ParsedBodyParamCollection(Map.of()),
                null,
                new UploadedFileCollection(Map.of()));
    }

    @Test
    void defaultsHaveEmptyParsedJson() {
        assertTrue(new JsonServerRequest().getParsedJson().getAll().isEmpty());
    }

    @Test
    void parsesJsonBodyWhenContentTypeIsJson() {
        var request = withJsonBody("{\"name\":\"bob\"}");

        assertEquals("bob", request.getParsedJson().getAll().get("name"));
    }

    @Test
    void leavesParsedJsonEmptyForMalformedBody() {
        var request = withJsonBody("not json");

        assertTrue(request.getParsedJson().getAll().isEmpty());
    }

    @Test
    void withParsedJsonReturnsCopy() {
        var collection = new ParsedJsonParamCollection(Map.of("a", "b"));

        assertSame(collection, new JsonServerRequest().withParsedJson(collection).getParsedJson());
    }

    private static JsonServerRequest build(
            String contentType, String body, java.util.Map<String, Object> parsedBody) {
        var stream = new Stream();
        if (!body.isEmpty()) {
            stream.write(body);
            stream.rewind();
        }
        var headers = new HeaderCollection(new Header(HeaderName.CONTENT_TYPE, contentType));
        return new JsonServerRequest(
                new Uri(),
                RequestMethod.POST,
                stream,
                headers,
                ProtocolVersion.V1_1,
                new ServerParamCollection(Map.of()),
                new CookieParamCollection(Map.of()),
                new QueryParamCollection(Map.of()),
                new ParsedBodyParamCollection(parsedBody),
                null,
                new UploadedFileCollection(Map.of()));
    }

    @Test
    void ignoresBodyWhenContentTypeIsNotJson() {
        assertTrue(
                build("text/plain", "{\"name\":\"bob\"}", Map.of())
                        .getParsedJson()
                        .getAll()
                        .isEmpty());
    }

    @Test
    void ignoresEmptyBodyWithJsonContentType() {
        assertTrue(
                build(ContentTypeValue.APPLICATION_JSON, "", Map.of())
                        .getParsedJson()
                        .getAll()
                        .isEmpty());
    }

    @Test
    void parsesJsonWhenParsedBodyAlreadyPresent() {
        var request =
                build(ContentTypeValue.APPLICATION_JSON, "{\"name\":\"bob\"}", Map.of("x", "y"));

        assertEquals("bob", request.getParsedJson().getAll().get("name"));
    }
}
