/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.param.AttributeParamCollection;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.uri.Uri;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ServerRequest}. */
final class ServerRequestTest {

    @Test
    void defaultsHaveEmptyParamCollections() {
        var request = new ServerRequest();

        assertTrue(request.getServerParams().getAll().isEmpty());
        assertTrue(request.getCookieParams().getAll().isEmpty());
        assertTrue(request.getQueryParams().getAll().isEmpty());
        assertTrue(request.getParsedBody().getAll().isEmpty());
        assertTrue(request.getUploadedFiles().getAll().isEmpty());
        assertTrue(request.getAttributes().getAll().isEmpty());
    }

    @Test
    void withParamCollectionsReturnCopies() {
        var request = new ServerRequest();
        var server = new ServerParamCollection(Map.of("k", "v"));
        var cookies = new CookieParamCollection(Map.of("c", "1"));
        var query = new QueryParamCollection(Map.of("q", "1"));
        var parsedBody = new ParsedBodyParamCollection(Map.of("b", "1"));
        var files = new UploadedFileCollection(Map.of());
        var attributes = new AttributeParamCollection();

        assertSame(server, request.withServerParams(server).getServerParams());
        assertSame(cookies, request.withCookieParams(cookies).getCookieParams());
        assertSame(query, request.withQueryParams(query).getQueryParams());
        assertSame(parsedBody, request.withParsedBody(parsedBody).getParsedBody());
        assertSame(files, request.withUploadedFiles(files).getUploadedFiles());
        assertSame(attributes, request.withAttributes(attributes).getAttributes());
    }

    @Test
    void isXmlHttpRequest() {
        var headers =
                new HeaderCollection(new Header(HeaderName.X_REQUESTED_WITH, "XMLHttpRequest"));
        var ajax =
                new ServerRequest(
                        new Uri(),
                        RequestMethod.GET,
                        new Stream(),
                        headers,
                        ProtocolVersion.V1_1,
                        new ServerParamCollection(Map.of()),
                        new CookieParamCollection(Map.of()),
                        new QueryParamCollection(Map.of()),
                        new ParsedBodyParamCollection(Map.of()),
                        new UploadedFileCollection(Map.of()),
                        null);

        assertTrue(ajax.isXmlHttpRequest());
        assertFalse(new ServerRequest().isXmlHttpRequest());
    }
}
