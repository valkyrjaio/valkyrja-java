/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.enum_.Scheme;
import org.junit.jupiter.api.Test;

/** Test the {@link Uri}. */
final class UriTest {

    private static Uri full() {
        return new Uri(Scheme.HTTPS, "user", "pass", "Example.com", 8080, "/path", "q=1", "frag");
    }

    @Test
    void defaultsAreEmpty() {
        var uri = new Uri();

        assertEquals(Scheme.EMPTY, uri.getScheme());
        assertEquals("", uri.getHost());
        assertEquals("", uri.getPath());
        assertFalse(uri.isSecure());
    }

    @Test
    void pathConstructor() {
        assertEquals("/path", new Uri("/path").getPath());
    }

    @Test
    void fullConstructorComputesUserInfoAndLowercasesHost() {
        var uri = full();

        assertEquals("user", uri.getUsername());
        assertEquals("pass", uri.getPassword());
        assertEquals("user:pass", uri.getUserInfo());
        assertEquals("example.com", uri.getHost());
        assertEquals("/path", uri.getPath());
        assertEquals("q=1", uri.getQuery());
        assertEquals("frag", uri.getFragment());
        assertTrue(uri.isSecure());
    }

    @Test
    void portFromSchemeWhenZero() {
        // Default scheme ports (443/80) are set internally but hidden by getPort() as "standard".
        assertTrue(new Uri(Scheme.HTTPS, "", "", "host", 0, "", "", "").hasPort());
        assertTrue(new Uri(Scheme.HTTP, "", "", "host", 0, "", "", "").hasPort());
        assertEquals(0, new Uri(Scheme.HTTPS, "", "", "host", 0, "", "", "").getPort());
    }

    @Test
    void authorityIncludesUserInfoAndNonStandardPort() {
        assertEquals("user:pass@example.com:8080", full().getAuthority());
        assertEquals("", new Uri().getAuthority());
    }

    @Test
    void standardPortIsHidden() {
        var standard = new Uri(Scheme.HTTPS, "", "", "host", 443, "", "", "");

        assertEquals(0, standard.getPort());
        assertEquals("host", standard.getAuthority());
    }

    @Test
    void hostPortAndSchemeHostPort() {
        var uri = full();

        assertTrue(uri.hasPort());
        assertEquals("example.com:8080", uri.getHostPort());
        assertEquals("https://example.com:8080", uri.getSchemeHostPort());

        // Empty scheme omits the scheme prefix from scheme-host-port.
        var schemeless = new Uri(Scheme.EMPTY, "", "", "host", 8080, "", "", "");
        assertEquals("host:8080", schemeless.getSchemeHostPort());
    }

    @Test
    void withMethodsReturnCopies() {
        var uri = new Uri("/path");

        assertEquals(Scheme.HTTPS, uri.withScheme(Scheme.HTTPS).getScheme());
        assertEquals("bob", uri.withUsername("bob").getUsername());
        assertEquals("secret", ((UriContract) uri.withUsername("bob")).withPassword("secret").getPassword());
        assertEquals("host", uri.withHost("host").getHost());
        assertEquals(9090, uri.withPort(9090).getPort());
        assertEquals("/other", uri.withPath("/other").getPath());
        assertEquals("a=b", uri.withQuery("?a=b").getQuery());
        assertEquals("frag", uri.withFragment("#frag").getFragment());
    }

    @Test
    void withUserInfoClearsPasswordWhenUserEmpty() {
        var uri = full().withUserInfo("", "ignored");

        assertEquals("", uri.getUsername());
        assertEquals("", uri.getPassword());
    }

    @Test
    void toStringRebuildsFullUri() {
        assertEquals("https://user:pass@example.com:8080/path?q=1#frag", full().toString());
    }
}