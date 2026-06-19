/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.request.Request;
import io.valkyrja.http.message.request.throwable.exception.HttpRequestInvalidRequestTargetException;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.uri.Uri;
import org.junit.jupiter.api.Test;

/** Test the {@link Request}. */
final class RequestTest {

    private static Request request(Uri uri) {
        return new Request(uri, RequestMethod.GET, new Stream(), new HeaderCollection());
    }

    @Test
    void defaults() {
        var request = new Request();

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
    }

    @Test
    void addsHostHeaderFromUri() {
        var request = request(new Uri(Scheme.HTTP, "", "", "example.com", 8080, "/p", "", ""));

        assertTrue(request.getHeaders().has("host"));
        assertEquals("example.com:8080", request.getHeaders().getHeaderLine("host"));
    }

    @Test
    void requestTargetIncludesQuery() {
        var request = request(new Uri(Scheme.HTTP, "", "", "h", 80, "/path", "a=1", ""));

        assertEquals("/path?a=1", request.getRequestTarget());
    }

    @Test
    void withRequestTargetOverridesAndValidates() {
        var request = new Request();

        assertEquals("/custom", request.withRequestTarget("/custom").getRequestTarget());
        assertThrows(
                HttpRequestInvalidRequestTargetException.class,
                () -> request.withRequestTarget("/has space"));
    }

    @Test
    void withMethod() {
        assertEquals(RequestMethod.POST, new Request().withMethod(RequestMethod.POST).getMethod());
    }

    @Test
    void withUriUpdatesHostHeaderUnlessPreserved() {
        var request = new Request();
        var uri = new Uri(Scheme.HTTP, "", "", "new-host.com", 0, "/", "", "");

        var updated = request.withUri(uri, false);
        assertEquals("new-host.com", updated.getHeaders().getHeaderLine("host"));

        // preserveHost keeps the existing host header.
        var withHost = request(new Uri(Scheme.HTTP, "", "", "orig.com", 0, "/", "", ""));
        var preserved = withHost.withUri(uri, true);
        assertEquals("orig.com", preserved.getHeaders().getHeaderLine("host"));
    }

    @Test
    void withUriWithoutHostDoesNotAddHostHeader() {
        var updated = new Request().withUri(new Uri("/just-path"), false);

        assertEquals("", updated.getHeaders().getHeaderLine("host"));
    }
}