/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.message.constant.HeaderValue;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link HeaderValue} constant holder. */
final class HeaderValueTest {

    @Test
    void exposesConstants() {
        assertEquals("Bearer", HeaderValue.BEARER);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<HeaderValue> constructor = HeaderValue.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
