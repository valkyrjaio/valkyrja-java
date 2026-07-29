/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.header.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.SameSite;
import io.valkyrja.http.message.header.value.Cookie;
import org.junit.jupiter.api.Test;

/** Test the {@link Cookie} header value. */
final class CookieTest {

    @Test
    void defaultsForNameOnly() {
        var cookie = new Cookie("session");

        assertEquals("session", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getExpire());
        assertEquals("/", cookie.getPath());
        assertEquals("", cookie.getDomain());
        assertFalse(cookie.isSecure());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isRaw());
        assertEquals(SameSite.LAX, cookie.getSameSite());
    }

    @Test
    void nameAndValueConstructor() {
        assertEquals("abc", new Cookie("session", "abc").getValue());
    }

    @Test
    void withMethodsReturnCopies() {
        var cookie =
                new Cookie("session")
                        .withName("sid")
                        .withValue("v")
                        .withExpire(123)
                        .withPath("/app")
                        .withDomain("example.com")
                        .withSecure(true)
                        .withHttpOnly(false)
                        .withRaw(true)
                        .withSameSite(SameSite.STRICT);

        assertEquals("sid", cookie.getName());
        assertEquals("v", cookie.getValue());
        assertEquals(123, cookie.getExpire());
        assertEquals("/app", cookie.getPath());
        assertEquals("example.com", cookie.getDomain());
        assertTrue(cookie.isSecure());
        assertFalse(cookie.isHttpOnly());
        assertTrue(cookie.isRaw());
        assertEquals(SameSite.STRICT, cookie.getSameSite());
    }

    @Test
    void getMaxAgeIsZeroWhenNoExpiry() {
        assertEquals(0, new Cookie("session").getMaxAge());
    }

    @Test
    void getMaxAgeIsPositiveForFutureExpiry() {
        int future = (int) (System.currentTimeMillis() / 1000) + 10_000;

        assertTrue(((Cookie) new Cookie("session").withExpire(future)).getMaxAge() > 0);
    }

    @Test
    void toStringRendersBasicCookie() {
        var rendered = new Cookie("session", "abc").toString();

        assertTrue(rendered.contains("session=abc"));
        assertTrue(rendered.contains("path=/"));
        assertTrue(rendered.contains("samesite=lax"));
    }

    @Test
    void toStringRendersAllAttributes() {
        int future = (int) (System.currentTimeMillis() / 1000) + 10_000;
        var cookie =
                (Cookie)
                        new Cookie("session", "abc")
                                .withExpire(future)
                                .withDomain("example.com")
                                .withSecure(true)
                                .withHttpOnly(true);

        var rendered = cookie.toString();
        assertTrue(rendered.contains("expires="));
        assertTrue(rendered.contains("max-age="));
        assertTrue(rendered.contains("domain=example.com"));
        assertTrue(rendered.contains("secure"));
        assertTrue(rendered.contains("httponly"));
    }

    @Test
    void deleteRendersDeletionCookie() {
        var rendered = ((Cookie) new Cookie("session", "abc").delete()).toString();

        assertTrue(rendered.contains("session=delete"));
        assertTrue(rendered.contains("max-age="));
    }

    @Test
    void httpOnlyComponentRenderingBranches() {
        assertTrue(
                ((Cookie) new Cookie("s", "v").withHttpOnly(true)).toString().contains("httponly"));
        assertFalse(
                ((Cookie) new Cookie("s", "v").withHttpOnly(false))
                        .toString()
                        .contains("httponly"));
    }
}
