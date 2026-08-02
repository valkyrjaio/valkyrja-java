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

import io.valkyrja.http.message.constant.HeaderName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link HeaderName} constant holder. */
final class HeaderNameTest {

    @Test
    void exposesConstants() {
        assertEquals("Accept", HeaderName.ACCEPT);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<HeaderName> constructor = HeaderName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
