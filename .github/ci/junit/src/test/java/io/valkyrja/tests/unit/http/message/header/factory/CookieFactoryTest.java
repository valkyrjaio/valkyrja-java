/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header.factory;

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
