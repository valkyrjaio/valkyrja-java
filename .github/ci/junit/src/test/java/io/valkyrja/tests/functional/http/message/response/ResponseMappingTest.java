/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.functional.http.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.enum_.StatusText;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.stream.Stream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Message-mapping fidelity for an outgoing HTTP response.
 *
 * <p>Asserts that a status code, headers, and body land on the framework's own {@link Response}
 * object and round-trip back out unchanged — including the {@link StatusCode} to reason-phrase
 * mapping across every defined code.
 *
 * <p>Mirrors the PHP reference {@code Tests\Functional\Http\Message\Response\ResponseMappingTest}.
 */
final class ResponseMappingTest {

    /** A representative code from each status class. */
    static java.util.stream.Stream<Arguments> provideRepresentativeStatusCodes() {
        return java.util.stream.Stream.of(
                Arguments.of("informational", StatusCode.CONTINUE, 100, "Continue"),
                Arguments.of("successful", StatusCode.OK, 200, "OK"),
                Arguments.of("created", StatusCode.CREATED, 201, "Created"),
                Arguments.of("no content", StatusCode.NO_CONTENT, 204, "No Content"),
                Arguments.of("redirection", StatusCode.MOVED_PERMANENTLY, 301, "Moved Permanently"),
                Arguments.of("not modified", StatusCode.NOT_MODIFIED, 304, "Not Modified"),
                Arguments.of("client error", StatusCode.NOT_FOUND, 404, "Not Found"),
                Arguments.of("unauthorized", StatusCode.UNAUTHORIZED, 401, "Unauthorized"),
                Arguments.of(
                        "server error",
                        StatusCode.INTERNAL_SERVER_ERROR,
                        500,
                        "Internal Server Error"));
    }

    private static Stream streamOf(String contents) {
        Stream stream = new Stream();
        stream.write(contents);
        stream.rewind();

        return stream;
    }

    /** Every status code resolves to the reason phrase its StatusText twin defines. */
    @ParameterizedTest
    @EnumSource(StatusCode.class)
    void testEveryStatusCodeMapsToItsReasonPhrase(StatusCode statusCode) {
        StatusText statusText = StatusText.valueOf(statusCode.name());

        Response response = new Response(new Stream(), statusCode, new HeaderCollection());

        assertEquals(statusCode, response.getStatusCode());
        assertEquals(statusCode.getValue(), response.getStatusCode().getValue());
        assertEquals(statusText.getValue(), statusCode.asPhrase());
        assertEquals(statusText.getValue(), response.getReasonPhrase());
    }

    /** A representative code from each class exposes its numeric code and phrase. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideRepresentativeStatusCodes")
    void testRepresentativeStatusCodesMapOntoResponse(
            String name, StatusCode statusCode, int expectedCode, String expectedPhrase) {
        Response response = new Response(new Stream(), statusCode, new HeaderCollection());

        assertEquals(expectedCode, response.getStatusCode().getValue());
        assertEquals(expectedCode, statusCode.code());
        assertEquals(expectedPhrase, response.getReasonPhrase());
    }

    /** Swapping the status code swaps the reason phrase with it. */
    @Test
    void testWithStatusCodeUpdatesTheReasonPhrase() {
        Response response = new Response(new Stream(), StatusCode.OK, new HeaderCollection());
        ResponseContract updated = response.withStatusCode(StatusCode.IM_USED);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        assertEquals(StatusCode.IM_USED, updated.getStatusCode());
        assertEquals("IM Used", updated.getReasonPhrase());
    }

    /**
     * A custom reason phrase overrides the default without changing the code, and an empty phrase
     * restores the code's own phrase.
     */
    @Test
    void testCustomReasonPhraseOverridesTheDefault() {
        Response response =
                new Response(new Stream(), StatusCode.NOT_FOUND, new HeaderCollection());
        ResponseContract custom = response.withReasonPhrase("Totally Missing");
        ResponseContract restored = custom.withReasonPhrase("");

        assertEquals("Not Found", response.getReasonPhrase());
        assertEquals("Totally Missing", custom.getReasonPhrase());
        assertEquals(StatusCode.NOT_FOUND, custom.getStatusCode());
        assertEquals("Not Found", restored.getReasonPhrase());
    }

    /** Headers supplied to the constructor round-trip back out, case-insensitively. */
    @Test
    void testHeadersRoundTripThroughTheResponse() {
        Response response =
                new Response(
                        new Stream(),
                        StatusCode.OK,
                        new HeaderCollection(
                                new Header("Content-Type", "application/json"),
                                new Header("Cache-Control", "no-cache", "no-store")));

        HeaderCollectionContract headers = response.getHeaders();

        assertEquals(
                List.of("content-type", "cache-control"), List.copyOf(headers.getAll().keySet()));
        assertTrue(headers.has("CONTENT-TYPE"));
        assertEquals("application/json", headers.getHeaderLine("Content-Type"));
        assertEquals("no-cache, no-store", headers.getHeaderLine("cache-control"));
        assertEquals(List.of("no-cache", "no-store"), headers.get("Cache-Control").getValues());

        ResponseContract added =
                (ResponseContract)
                        response.withHeaders(
                                headers.withAddedHeaders(
                                        new Header("CACHE-CONTROL", "must-revalidate")));

        assertEquals(
                "no-cache, no-store, must-revalidate",
                added.getHeaders().getHeaderLine("Cache-Control"));
        assertEquals("no-cache, no-store", response.getHeaders().getHeaderLine("Cache-Control"));

        ResponseContract removed =
                (ResponseContract) response.withHeaders(headers.withoutHeader("Content-Type"));

        assertFalse(removed.getHeaders().has("content-type"));
        assertEquals("", removed.getHeaders().getHeaderLine("Content-Type"));
    }

    /** A body supplied to the constructor round-trips back out unchanged. */
    @Test
    void testBodyRoundTripsThroughTheConstructor() {
        Response response =
                new Response(streamOf("{\"ok\":true}"), StatusCode.OK, new HeaderCollection());

        assertEquals("{\"ok\":true}", response.getBody().getContents());
        assertEquals("{\"ok\":true}", response.getBody().toString());
    }

    /** The create() factory writes content into a rewound body stream. */
    @Test
    void testBodyRoundTripsThroughTheCreateFactory() {
        Response response =
                Response.create(
                        "plain content",
                        StatusCode.ACCEPTED,
                        new HeaderCollection(new Header("X-Trace", "abc")));

        assertEquals("plain content", response.getBody().getContents());
        assertEquals(StatusCode.ACCEPTED, response.getStatusCode());
        assertEquals("Accepted", response.getReasonPhrase());
        assertEquals("abc", response.getHeaders().getHeaderLine("x-trace"));
    }

    /** create() with nothing supplied yields an empty 200 response. */
    @Test
    void testCreateDefaults() {
        Response response = Response.create(null, null, null);

        assertEquals("", response.getBody().getContents());
        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        assertEquals(Map.of(), response.getHeaders().getAll());
        assertEquals(ProtocolVersion.V1_1, response.getProtocolVersion());
    }

    /** Swapping the body leaves the original response untouched. */
    @Test
    void testWithBodyLeavesTheOriginalUntouched() {
        Response response = Response.create("original", null, null);

        ResponseContract updated = (ResponseContract) response.withBody(streamOf("replacement"));

        assertEquals("replacement", updated.getBody().toString());
        assertEquals("original", response.getBody().toString());
    }

    /** The protocol version round-trips. */
    @Test
    void testProtocolVersionRoundTrips() {
        Response response = Response.create(null, null, null);
        ResponseContract updated =
                (ResponseContract) response.withProtocolVersion(ProtocolVersion.V2);

        assertEquals(ProtocolVersion.V1_1, response.getProtocolVersion());
        assertEquals(ProtocolVersion.V2, updated.getProtocolVersion());
    }
}
