/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.routing.constant.OptionName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionName} constant holder. */
final class OptionNameTest {

    @Test
    void exposesConstants() {
        assertEquals("help", OptionName.HELP);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<OptionName> constructor = OptionName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
