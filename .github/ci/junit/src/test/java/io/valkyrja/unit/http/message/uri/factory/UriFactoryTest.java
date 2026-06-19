/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.uri.factory.UriFactory;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidFromStringException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPathException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidQueryException;
import org.junit.jupiter.api.Test;

/** Test the {@link UriFactory}. */
final class UriFactoryTest {

    @Test
    void fromStringParsesFullUrl() {
        var uri = UriFactory.fromString("https://user:pass@example.com:8080/path?q=1#frag");

        assertEquals(Scheme.HTTPS, uri.getScheme());
        assertEquals("user", uri.getUsername());
        assertEquals("pass", uri.getPassword());
        assertEquals("example.com", uri.getHost());
        assertEquals("/path", uri.getPath());
        assertEquals("q=1", uri.getQuery());
        assertEquals("frag", uri.getFragment());
    }

    @Test
    void fromStringParsesUserOnlyInfo() {
        assertEquals("user", UriFactory.fromString("http://user@example.com/").getUsername());
    }

    @Test
    void fromStringPrefixesBareAuthority() {
        assertEquals("example.com", UriFactory.fromString("example.com").getHost());
    }

    @Test
    void fromStringParsesAbsolutePath() {
        assertEquals("/path", UriFactory.fromString("/path").getPath());
    }

    @Test
    void fromStringThrowsForInvalidUri() {
        assertThrows(
                HttpUriInvalidFromStringException.class, () -> UriFactory.fromString("http://a b"));
    }

    @Test
    void filterScheme() {
        assertEquals(Scheme.HTTP, UriFactory.filterScheme("HTTP:"));
        assertEquals(Scheme.HTTPS, UriFactory.filterScheme("https"));
        assertEquals(Scheme.EMPTY, UriFactory.filterScheme("ftp"));
    }

    @Test
    void validatePortRejectsInvalid() {
        assertThrows(HttpUriInvalidPortException.class, () -> UriFactory.validatePort(99999));
    }

    @Test
    void filterPath() {
        assertEquals("/foo", UriFactory.filterPath("//foo"));
        assertThrows(HttpUriInvalidPathException.class, () -> UriFactory.filterPath("/foo?bar"));
        assertThrows(HttpUriInvalidPathException.class, () -> UriFactory.filterPath("/foo#bar"));
    }

    @Test
    void filterQuery() {
        assertEquals("a=1", UriFactory.filterQuery("?a=1"));
        assertThrows(HttpUriInvalidQueryException.class, () -> UriFactory.filterQuery("a=1#frag"));
    }

    @Test
    void filterFragmentStripsLeadingHash() {
        assertEquals("frag", UriFactory.filterFragment("#frag"));
    }

    @Test
    void isStandardPort() {
        assertTrue(UriFactory.isStandardPort(Scheme.HTTP, "host", 80));
        assertTrue(UriFactory.isStandardPort(Scheme.HTTPS, "host", 443));
        assertFalse(UriFactory.isStandardPort(Scheme.HTTP, "host", 8080));
        assertTrue(UriFactory.isStandardPort(Scheme.EMPTY, "host", 0));
        assertTrue(UriFactory.isStandardPort(Scheme.HTTP, "", 80));
    }

    @Test
    void toStringDelegatesToParts() {
        var uri = new Uri(Scheme.HTTP, "", "", "host", 8080, "/p", "q=1", "f");

        assertEquals("http://host:8080/p?q=1#f", UriFactory.toString(uri));
    }

    @Test
    void toStringEmptyUriHasNoParts() {
        // Empty scheme/authority/path/query/fragment exercise every part's empty-return path.
        assertEquals("", UriFactory.toString(new Uri()));
    }

    @Test
    void toStringPrependsSlashToRelativePath() {
        assertEquals("/relative", UriFactory.toString(new Uri("relative")));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new UriFactory() {});
    }
}
