/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.request.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.request.data.ParsedRequestBody;
import io.valkyrja.http.message.request.factory.RequestBodyFactory;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestBodyFactory}. */
final class RequestBodyFactoryTest {

    @Test
    void parseUrlEncodedReturnsEmptyForNull() {
        assertTrue(RequestBodyFactory.parseUrlEncoded(null).isEmpty());
    }

    @Test
    void parseUrlEncodedReturnsEmptyForBlank() {
        assertTrue(RequestBodyFactory.parseUrlEncoded("").isEmpty());
    }

    @Test
    void parseUrlEncodedDecodesPairsAndToleratesGaps() {
        // Leading '?', a valued pair, a valueless pair, an empty pair, and a percent-encoded value.
        var params = RequestBodyFactory.parseUrlEncoded("?a=1&flag&&b=two%20words");

        assertEquals(3, params.size());
        assertEquals("1", params.get("a"));
        assertEquals("", params.get("flag"));
        assertEquals("two words", params.get("b"));
    }

    @Test
    void parseUrlEncodedBodyPopulatesParsedBody() {
        var parsed = RequestBodyFactory.parse("application/x-www-form-urlencoded", "a=1&b=2");

        assertEquals("1", parsed.parsedBody().get("a"));
        assertEquals("2", parsed.parsedBody().get("b"));
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseIgnoresUnhandledContentType() {
        var parsed = RequestBodyFactory.parse("application/json", "{\"a\":1}");

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseHandlesNullContentTypeAndBody() {
        var parsed = RequestBodyFactory.parse(null, null);

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseMultipartWithoutBoundaryIsEmpty() {
        // Multipart content type but no boundary token.
        var parsed = RequestBodyFactory.parse("multipart/form-data", "ignored");

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseMultipartWithEmptyBodyIsEmpty() {
        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=B", "");

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseMultipartExtractsFieldsAndFiles() {
        String body =
                "--B\r\n"
                        + "Content-Disposition: form-data; name=\"field\"\r\n"
                        + "X-Extra: ignored\r\n"
                        + "\r\n"
                        + "value1\r\n"
                        + "--B\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\"a.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "file contents\r\n"
                        + "--B--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=B", body);

        assertEquals("value1", parsed.parsedBody().get("field"));

        UploadedFileContract file = parsed.files().get("file");
        assertNotNull(file);
        assertEquals("a.txt", file.getClientFilename());
        assertEquals("text/plain", file.getClientMediaType());
        assertEquals("file contents", file.getStream().getContents());
    }

    @Test
    void parseMultipartHandlesLineFeedOnlyNewlines() {
        // LF-only separators exercise the \n leading strip, the \n\n header separator, and the \n
        // trailing strip.
        String body =
                "--C\n" + "Content-Disposition: form-data; name=\"f\"\n" + "\n" + "v\n" + "--C--\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=C", body);

        assertEquals("v", parsed.parsedBody().get("f"));
    }

    @Test
    void parseMultipartSkipsPartWithoutHeaderSeparator() {
        String body = "--D\r\n" + "garbage-no-separator\r\n" + "--D--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=D", body);

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseMultipartSkipsPartWithoutName() {
        String body =
                "--E\r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "val\r\n" + "--E--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=E", body);

        assertTrue(parsed.parsedBody().isEmpty());
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void parseMultipartFileWithoutContentTypeHasBlankMediaType() {
        String body =
                "--F\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\"b.bin\"\r\n"
                        + "\r\n"
                        + "data\r\n"
                        + "--F--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=F", body);

        UploadedFileContract file = parsed.files().get("file");
        assertNotNull(file);
        assertEquals("b.bin", file.getClientFilename());
        assertEquals("", file.getClientMediaType());
    }

    @Test
    void parseMultipartToleratesSegmentWithoutLeadingNewline() {
        // Malformed: no CRLF between the boundary and the part headers.
        String body =
                "--G"
                        + "Content-Disposition: form-data; name=\"x\"\r\n"
                        + "\r\n"
                        + "xv\r\n"
                        + "--G--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=G", body);

        assertEquals("xv", parsed.parsedBody().get("x"));
    }

    @Test
    void parseMultipartToleratesSegmentWithoutTrailingNewline() {
        // Malformed: no CRLF between the part content and the closing boundary.
        String body =
                "--H\r\n"
                        + "Content-Disposition: form-data; name=\"y\"\r\n"
                        + "\r\n"
                        + "yv"
                        + "--H--\r\n";

        var parsed = RequestBodyFactory.parse("multipart/form-data; boundary=H", body);

        assertEquals("yv", parsed.parsedBody().get("y"));
    }

    @Test
    void parsedRequestBodyDefensivelyCopies() {
        var parsed = new ParsedRequestBody(java.util.Map.of("a", "1"), java.util.Map.of());

        assertEquals("1", parsed.parsedBody().get("a"));
        assertTrue(parsed.files().isEmpty());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new RequestBodyFactory() {});
    }
}
