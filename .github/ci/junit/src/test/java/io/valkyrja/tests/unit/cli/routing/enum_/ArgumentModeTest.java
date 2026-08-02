/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.enum_.ArgumentMode;
import org.junit.jupiter.api.Test;

/** Test the {@link ArgumentMode}. */
final class ArgumentModeTest {

    @Test
    void exposesAllConstants() {
        assertEquals(2, ArgumentMode.values().length);
        assertSame(ArgumentMode.REQUIRED, ArgumentMode.valueOf("REQUIRED"));
    }
}
