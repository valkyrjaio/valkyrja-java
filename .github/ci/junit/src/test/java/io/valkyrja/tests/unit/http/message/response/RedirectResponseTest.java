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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.RedirectResponse;
import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidRedirectStatusCodeException;
import io.valkyrja.http.message.uri.Uri;
import org.junit.jupiter.api.Test;

/** Test the {@link RedirectResponse}. */
final class RedirectResponseTest {

    private static RedirectResponse response(String path) {
        return new RedirectResponse(new Uri(path), StatusCode.FOUND, new HeaderCollection());
    }

    private static ServerRequestContract requestWithReferer(String referer) {
        var request = mock(ServerRequestContract.class);
        when(request.getUri())
                .thenReturn(new Uri("https://example.com/page".replace("https://", "")));
        var headers = mock(HeaderCollectionContract.class);
        when(headers.getHeaderLine("Referer")).thenReturn(referer);
        when(request.getHeaders()).thenReturn(headers);
        return request;
    }

    @Test
    void defaultRedirectsToRootWithFoundStatus() {
        var response = new RedirectResponse();

        assertEquals(StatusCode.FOUND, response.getStatusCode());
        assertEquals("/", response.getUri().getPath());
        assertTrue(response.getHeaders().has("location"));
    }

    @Test
    void rejectsNonRedirectStatus() {
        assertThrows(
                HttpRequestInvalidRedirectStatusCodeException.class,
                () -> new RedirectResponse(new Uri("/x"), StatusCode.OK, new HeaderCollection()));
    }

    @Test
    void emptyUriProducesRootLocationHeader() {
        var response = new RedirectResponse(new Uri(), StatusCode.FOUND, new HeaderCollection());

        assertTrue(response.getHeaders().getHeaderLine("location").contains("/"));
    }

    @Test
    void createFromUriDefaults() {
        var response = RedirectResponse.createFromUri(null, null, null);

        assertEquals(StatusCode.FOUND, response.getStatusCode());
    }

    @Test
    void withUriUpdatesLocation() {
        var response = response("/old").withUri(new Uri("/new"));

        assertEquals("/new", response.getUri().getPath());
        assertTrue(response.getHeaders().getHeaderLine("location").contains("/new"));
    }

    @Test
    void secureBuildsHttpsUri() {
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri("example.com"));

        var response = response("/old").secure("/secure", request);

        assertTrue(response.getUri().isSecure());
        assertEquals("/secure", response.getUri().getPath());
    }

    @Test
    void backUsesInternalReferer() {
        var response = response("/old").back(requestWithReferer("/internal"));

        assertEquals("/internal", response.getUri().getPath());
    }

    @Test
    void backFallsBackToRootWhenNoReferer() {
        var response = response("/old").back(requestWithReferer(""));

        assertEquals("/", response.getUri().getPath());
    }

    @Test
    void backUsesRefererBranches() {
        var request = mock(ServerRequestContract.class);
        var headers = mock(HeaderCollectionContract.class);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getUri())
                .thenReturn(
                        new io.valkyrja.http.message.uri.Uri(
                                io.valkyrja.http.message.uri.enum_.Scheme.HTTPS,
                                "",
                                "",
                                "example.com",
                                0,
                                "/",
                                "",
                                ""));
        var redirect = new RedirectResponse();

        when(headers.getHeaderLine("Referer")).thenReturn("");
        assertTrue(redirect.back(request).getHeaders().getHeaderLine("Location").endsWith("/"));

        when(headers.getHeaderLine("Referer")).thenReturn("https://example.com/page");
        assertTrue(redirect.back(request).getHeaders().getHeaderLine("Location").contains("/page"));

        when(headers.getHeaderLine("Referer")).thenReturn("https://evil.com/page");
        assertFalse(redirect.back(request).getHeaders().getHeaderLine("Location").contains("evil"));
    }

    @Test
    void backTreatsNullRefererAsRoot() {
        var request = mock(ServerRequestContract.class);
        var headers = mock(HeaderCollectionContract.class);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getUri())
                .thenReturn(
                        new io.valkyrja.http.message.uri.Uri(
                                io.valkyrja.http.message.uri.enum_.Scheme.HTTPS,
                                "",
                                "",
                                "example.com",
                                0,
                                "/",
                                "",
                                ""));
        when(headers.getHeaderLine("Referer")).thenReturn(null);

        assertTrue(
                new RedirectResponse()
                        .back(request)
                        .getHeaders()
                        .getHeaderLine("Location")
                        .endsWith("/"));
    }
}
