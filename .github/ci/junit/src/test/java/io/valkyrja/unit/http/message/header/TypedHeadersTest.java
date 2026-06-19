/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.ContentType;
import io.valkyrja.http.message.header.Location;
import io.valkyrja.http.message.header.Referer;
import io.valkyrja.http.message.header.SetCookie;
import io.valkyrja.http.message.header.value.Cookie;
import org.junit.jupiter.api.Test;

/** Test the typed {@link io.valkyrja.http.message.header.Header} subclasses. */
final class TypedHeadersTest {

    @Test
    void contentType() {
        var header = new ContentType("text/html");

        assertEquals(HeaderName.CONTENT_TYPE, header.getName());
        assertTrue(header.getHeaderLine().contains("text/html"));
    }

    @Test
    void location() {
        assertEquals(HeaderName.LOCATION, new Location("/home").getName());
    }

    @Test
    void referer() {
        assertEquals(HeaderName.REFERER, new Referer("/back").getName());
    }

    @Test
    void setCookie() {
        var header = new SetCookie(new Cookie("session", "abc"));

        assertEquals(HeaderName.SET_COOKIE, header.getName());
        assertTrue(header.getHeaderLine().contains("session=abc"));
    }
}