/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import org.junit.jupiter.api.Test;

/** Test the {@link BackgroundColor} enum. */
final class BackgroundColorTest {

    @Test
    void valueAndDefault() {
        assertEquals(41, BackgroundColor.RED.value);
        assertEquals(49, BackgroundColor.RED.getDefault());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (BackgroundColor color : BackgroundColor.values()) {
            assertSame(color, BackgroundColor.valueOf(color.name()));
        }
    }
}
