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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.value.Cookie;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.stream.Stream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link Response} and the abstract {@code Message} base. */
final class ResponseTest {

    private static String captureStdout(Supplier<?> action) {
        var original = System.out;
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.get();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void defaults() {
        var response = new Response();

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals(StatusCode.OK.asPhrase(), response.getReasonPhrase());
        assertEquals(ProtocolVersion.V1_1, response.getProtocolVersion());
    }

    @Test
    void createWritesContent() {
        var response = Response.create("hello", StatusCode.CREATED, null);

        assertEquals(StatusCode.CREATED, response.getStatusCode());
        assertEquals("hello", response.getBody().toString());
    }

    @Test
    void statusCodeAndReasonPhrase() {
        var response = new Response();

        assertEquals(StatusCode.NOT_FOUND, response.withStatusCode(StatusCode.NOT_FOUND).getStatusCode());
        assertEquals("Custom", response.withReasonPhrase("Custom").getReasonPhrase());
        // Empty reason phrase falls back to the status code's phrase.
        assertEquals(StatusCode.OK.asPhrase(), response.withReasonPhrase("").getReasonPhrase());
    }

    @Test
    void messageBaseAccessors() {
        var response = new Response();
        var headers = new HeaderCollection(new Header("X-Test", "v"));
        var body = new Stream();
        body.write("body");

        var withHeaders = response.withHeaders(headers);
        assertSame(headers, withHeaders.getHeaders());
        assertEquals(ProtocolVersion.V2, response.withProtocolVersion(ProtocolVersion.V2).getProtocolVersion());
        assertEquals("body", response.withBody(body).getBody().toString());
    }

    @Test
    void cookies() {
        var response = new Response();

        var withCookie = response.withCookie(new Cookie("session", "abc"));
        assertTrue(withCookie.getHeaders().has("set-cookie"));

        var withoutCookie = response.withoutCookie(new Cookie("session", "abc"));
        assertTrue(withoutCookie.getHeaders().getHeaderLine("set-cookie").contains("delete"));
    }

    @Test
    void sendOutputsHttpLineHeadersAndBody() {
        var response =
                Response.create("payload", StatusCode.OK, new HeaderCollection(new Header("X-Test", "v")));

        var output = captureStdout(response::send);

        assertTrue(output.contains("HTTP/"));
        assertTrue(output.contains("X-Test: v"));
        assertTrue(output.contains("payload"));
    }

    @Test
    void withReasonPhraseNullFallsBackToStatusPhrase() {
        assertEquals(StatusCode.OK.asPhrase(), new Response().withReasonPhrase(null).getReasonPhrase());
    }

    @Test
    void sendMethodsCoverPhraseAndSeekableBranches() {
        var original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
        try {
            new Response().sendHttpLine();
            new Response().withReasonPhrase("Custom").sendHttpLine();
            new Response().sendBody();
            var closed = new Stream();
            closed.close();
            ((io.valkyrja.http.message.response.contract.ResponseContract)
                            new Response().withBody(closed))
                    .sendBody();
        } finally {
            System.setOut(original);
        }
    }

}
