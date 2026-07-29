/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.SetCookie;
import io.valkyrja.http.message.header.value.Cookie;
import org.junit.jupiter.api.Test;

/** Test the {@link SetCookie}. */
final class SetCookieTest {

    @Test
    void usesSetCookieNameAndSerializesCookie() {
        var header = new SetCookie(new Cookie("session", "abc"));

        assertEquals(HeaderName.SET_COOKIE, header.getName());
        assertTrue(header.getHeaderLine().contains("session=abc"));
    }
}
