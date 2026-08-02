/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.request.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.contract.JsonServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestFactory}. */
final class RequestFactoryTest {

    @Test
    void fromGlobalsWithDefaults() {
        var request = RequestFactory.fromGlobals();

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals(ProtocolVersion.V1_1, request.getProtocolVersion());
    }

    @Test
    void fromGlobalsParsesServerData() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_METHOD", "POST");
        server.put("SERVER_NAME", "example.com");
        server.put("REQUEST_URI", "/path");
        server.put("SERVER_PROTOCOL", "HTTP/1.1");

        var request = RequestFactory.fromGlobals(server, null);

        assertEquals(RequestMethod.POST, request.getMethod());
        assertEquals("example.com", request.getUri().getHost());
        assertTrue(request.getServerParams().getAll().containsKey("SERVER_NAME"));
    }

    @Test
    void fromGlobalsDerivesQueryFromQueryString() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("QUERY_STRING", "q=1&r=two");

        var request = RequestFactory.fromGlobals(server, null);

        assertEquals("1", request.getQueryParams().getAll().get("q"));
        assertEquals("two", request.getQueryParams().getAll().get("r"));
    }

    @Test
    void fromGlobalsParsesCookieHeader() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("HTTP_COOKIE", "session=abc; theme=dark");

        var request = RequestFactory.fromGlobals(server, null);

        assertEquals("abc", request.getCookieParams().getAll().get("session"));
    }

    @Test
    void fromGlobalsSetsRawBodyStreamAndParsesFormBody() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("CONTENT_TYPE", "application/x-www-form-urlencoded");

        var request = RequestFactory.fromGlobals(server, "a=1&b=2");

        assertEquals("a=1&b=2", request.getBody().getContents());
        assertEquals("1", request.getParsedBody().getAll().get("a"));
        assertEquals("2", request.getParsedBody().getAll().get("b"));
    }

    @Test
    void fromGlobalsParsesMultipartFilesFromBody() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("CONTENT_TYPE", "multipart/form-data; boundary=B");
        String body =
                "--B\r\n"
                        + "Content-Disposition: form-data; name=\"field\"\r\n"
                        + "\r\n"
                        + "v\r\n"
                        + "--B\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\"a.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "data\r\n"
                        + "--B--\r\n";

        var request = RequestFactory.fromGlobals(server, body);

        assertEquals("v", request.getParsedBody().getAll().get("field"));
        assertTrue(request.getUploadedFiles().getAll().containsKey("file"));
    }

    @Test
    void fromGlobalsRejectsInvalidProtocol() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("SERVER_PROTOCOL", "garbage");

        assertThrows(
                IllegalArgumentException.class, () -> RequestFactory.fromGlobals(server, null));
    }

    @Test
    void jsonFromGlobalsParsesJsonBodyAndDerivesQuery() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_METHOD", "POST");
        server.put("QUERY_STRING", "q=1");
        server.put("HTTP_COOKIE", "session=abc");
        server.put("CONTENT_TYPE", "application/json");

        var request = RequestFactory.jsonFromGlobals(server, "{\"b\":\"1\"}");

        assertInstanceOf(JsonServerRequest.class, request);
        assertEquals("1", request.getQueryParams().getAll().get("q"));
        assertEquals("abc", request.getCookieParams().getAll().get("session"));
        assertEquals("1", ((JsonServerRequestContract) request).getParsedJson().getAll().get("b"));
    }

    @Test
    void jsonFromGlobalsWithoutCookies() {
        var request = RequestFactory.jsonFromGlobals(new LinkedHashMap<>(), null);

        assertTrue(request.getCookieParams().getAll().isEmpty());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new RequestFactory() {});
    }
}
