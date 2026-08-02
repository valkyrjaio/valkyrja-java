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

import io.valkyrja.cli.routing.enum_.OptionMode;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionMode}. */
final class OptionModeTest {

    @Test
    void exposesAllConstants() {
        assertEquals(2, OptionMode.values().length);
        assertSame(OptionMode.REQUIRED, OptionMode.valueOf("REQUIRED"));
    }
}
