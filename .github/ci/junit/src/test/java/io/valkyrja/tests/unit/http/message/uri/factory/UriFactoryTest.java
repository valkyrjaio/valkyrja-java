/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.uri.factory;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest(name = "filterUserInfo {0}")
    @CsvSource(
            delimiter = '|',
            quoteCharacter = '"',
            textBlock =
                    """
                    keeps the unreserved characters  | aZ0-_.~     | aZ0-_.~
                    keeps the sub delimiters         | !$&'()*+,;= | !$&'()*+,;=
                    keeps the username separator     | user:pass   | user:pass
                    encodes a space                  | user name   | user%20name
                    encodes an at sign               | user:p@ss   | user:p%40ss
                    encodes a forward slash          | user/name   | user%2Fname
                    encodes a question mark          | user?name   | user%3Fname
                    encodes a multibyte character    | usér        | us%C3%A9r
                    keeps a valid triplet            | us%C3%A9r   | us%C3%A9r
                    uppercases a triplet             | us%c3%a9r   | us%C3%A9r
                    encodes a lone percent sign      | 100%        | 100%25
                    encodes an incomplete triplet    | %2          | %252
                    encodes a non hexadecimal escape | %zz         | %25zz
                    """)
    void filterUserInfoEncodes(String name, String userInfo, String expected) {
        assertEquals(expected, UriFactory.filterUserInfo(userInfo));
    }

    @ParameterizedTest(name = "filterPath {0}")
    @CsvSource(
            delimiter = '|',
            quoteCharacter = '"',
            textBlock =
                    """
                    keeps the unreserved characters | /aZ0-_.~     | /aZ0-_.~
                    keeps the sub delimiters        | /!$&'()*+,;= | /!$&'()*+,;=
                    keeps a colon and an at sign    | /a:b@c       | /a:b@c
                    keeps the segment separator     | /a/b/c       | /a/b/c
                    encodes a space                 | /foo bar     | /foo%20bar
                    encodes a multibyte character   | /café        | /caf%C3%A9
                    keeps a valid triplet           | /foo%20bar   | /foo%20bar
                    uppercases a triplet            | /foo%2fbar   | /foo%2Fbar
                    encodes a lone percent sign     | /100%/x      | /100%25/x
                    encodes a bracket               | /a[b]c       | /a%5Bb%5Dc
                    normalizes the leading slashes  | ///a b       | /a%20b
                    keeps a relative path           | a b          | a%20b
                    """)
    void filterPathEncodes(String name, String path, String expected) {
        assertEquals(expected, UriFactory.filterPath(path));
    }

    @ParameterizedTest(name = "filterQuery {0}")
    @CsvSource(
            delimiter = '|',
            quoteCharacter = '"',
            textBlock =
                    """
                    keeps the unreserved characters | a=Z0-_.~    | a=Z0-_.~
                    keeps the sub delimiters        | !$&'()*+,;= | !$&'()*+,;=
                    keeps a colon and an at sign    | a=b:c@d     | a=b:c@d
                    keeps a slash                   | a=b/c       | a=b/c
                    keeps an inner question mark    | ?a=b?c      | a=b?c
                    encodes a space                 | a=b c&d=e   | a=b%20c&d=e
                    encodes a multibyte character   | a=café      | a=caf%C3%A9
                    keeps a valid triplet           | a=%C3%A9    | a=%C3%A9
                    uppercases a triplet            | a=%c3%a9    | a=%C3%A9
                    encodes a lone percent sign     | a=100%      | a=100%25
                    encodes a bracket               | a[]=b       | a%5B%5D=b
                    """)
    void filterQueryEncodes(String name, String query, String expected) {
        assertEquals(expected, UriFactory.filterQuery(query));
    }

    @ParameterizedTest(name = "filterFragment {0}")
    @CsvSource(
            delimiter = '|',
            quoteCharacter = '"',
            textBlock =
                    """
                    keeps the unreserved characters | aZ0-_.~ | aZ0-_.~
                    keeps a colon and an at sign    | a:b@c   | a:b@c
                    keeps a slash and a question    | a/b?c   | a/b?c
                    encodes a space                 | #a b    | a%20b
                    encodes a multibyte character   | café    | caf%C3%A9
                    keeps a valid triplet           | %C3%A9  | %C3%A9
                    uppercases a triplet            | %c3%a9  | %C3%A9
                    encodes a lone percent sign     | 100%    | 100%25
                    """)
    void filterFragmentEncodes(String name, String fragment, String expected) {
        assertEquals(expected, UriFactory.filterFragment(fragment));
    }

    @ParameterizedTest(name = "filterHost {0}")
    @CsvSource(
            delimiter = '|',
            quoteCharacter = '"',
            textBlock =
                    """
                    lowercases the reg name       | EXAMPLE.COM   | example.com
                    keeps the sub delimiters      | a!$&'()*+,;=b | a!$&'()*+,;=b
                    encodes a space               | exa mple.com  | exa%20mple.com
                    encodes a colon               | example.com:x | example.com%3Ax
                    encodes a multibyte character | café.com      | caf%C3%A9.com
                    keeps a valid triplet         | caf%C3%A9.com | caf%C3%A9.com
                    encodes a lone percent sign   | 100%.com      | 100%25.com
                    """)
    void filterHostEncodes(String name, String host, String expected) {
        assertEquals(expected, UriFactory.filterHost(host));
    }

    @Test
    void filterHostIsEmptyForAnEmptyHost() {
        assertEquals("", UriFactory.filterHost(""));
    }

    /** An IP literal is in brackets, and it holds colons that a reg-name does not allow. */
    @Test
    void filterHostKeepsIpLiteral() {
        assertEquals("[::1]", UriFactory.filterHost("[::1]"));
        assertEquals("[2001:db8::ff00:42:8329]", UriFactory.filterHost("[2001:DB8::FF00:42:8329]"));
        // A bracket on one side only does not make an IP literal, so the value is a reg-name.
        assertEquals("%5B%3A%3A1", UriFactory.filterHost("[::1"));
        assertEquals("%3A%3A1%5D", UriFactory.filterHost("::1]"));
    }

    /** A value that arrives already encoded keeps its meaning through a second filter pass. */
    @Test
    void filterIsIdempotent() {
        String path = UriFactory.filterPath("/foo bar/100%");
        String query = UriFactory.filterQuery("a=b c&d=100%");
        String fragment = UriFactory.filterFragment("a b 100%");
        String userInfo = UriFactory.filterUserInfo("user:p@ss word");
        String host = UriFactory.filterHost("exa mple.com");

        assertEquals(path, UriFactory.filterPath(path));
        assertEquals(query, UriFactory.filterQuery(query));
        assertEquals(fragment, UriFactory.filterFragment(fragment));
        assertEquals(userInfo, UriFactory.filterUserInfo(userInfo));
        assertEquals(host, UriFactory.filterHost(host));
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

    @Test
    void fromStringEmptyAndHttpsAndOpaqueBranches() {
        assertNotNull(UriFactory.fromString(""));
        assertNotNull(UriFactory.fromString("https://host/path"));
        // Opaque URI has a null raw path, exercising the path null-default.
        assertNotNull(UriFactory.fromString("http:opaque"));
    }

    @Test
    void isStandardPortHostAndPortBranches() {
        assertTrue(UriFactory.isStandardPort(Scheme.HTTP, "", 80));
        assertTrue(UriFactory.isStandardPort(Scheme.HTTP, "host", 0));
        assertFalse(UriFactory.isStandardPort(Scheme.HTTP, "host", 8080));
    }
}
