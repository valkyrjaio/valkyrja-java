/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.server.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.server.constant.CliCommandName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link CliCommandName} constant holder. */
final class CliCommandNameTest {

    @Test
    void exposesConstants() {
        assertEquals("help", CliCommandName.HELP);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<CliCommandName> constructor = CliCommandName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
