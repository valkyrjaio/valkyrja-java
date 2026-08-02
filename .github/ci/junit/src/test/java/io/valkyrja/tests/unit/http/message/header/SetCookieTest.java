/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
