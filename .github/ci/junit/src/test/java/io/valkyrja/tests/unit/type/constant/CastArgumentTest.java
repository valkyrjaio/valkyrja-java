/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.type.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.type.constant.CastArgument;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link CastArgument} constant holder. */
final class CastArgumentTest {

    @Test
    void exposesConstants() {
        assertEquals("value", CastArgument.VALUE);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<CastArgument> constructor = CastArgument.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
