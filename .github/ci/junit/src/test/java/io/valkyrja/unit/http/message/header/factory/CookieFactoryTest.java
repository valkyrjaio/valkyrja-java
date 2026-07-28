/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.message.header.factory.CookieFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link CookieFactory}. */
final class CookieFactoryTest {

    @Test
    void parseCookieHeaderExtractsNameValuePairs() {
        var cookies = CookieFactory.parseCookieHeader("session=abc; theme=dark");

        assertEquals("abc", cookies.get("session"));
        assertEquals("dark", cookies.get("theme"));
    }

    @Test
    void combineKeyAndValue() {
        assertEquals("k=v", CookieFactory.combineKeyAndValue("k", "v"));
    }

    @Test
    void convertCookieArrayToHeaderString() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("session", "abc");
        cookies.put("theme", "dark");

        assertEquals(
                "session=abc; theme=dark", CookieFactory.convertCookieArrayToHeaderString(cookies));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new CookieFactory() {});
    }
}
