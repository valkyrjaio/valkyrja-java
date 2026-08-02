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

import io.valkyrja.cli.interaction.enum_.TextColor;
import org.junit.jupiter.api.Test;

/** Test the {@link TextColor} enum. */
final class TextColorTest {

    @Test
    void valueAndDefault() {
        assertEquals(31, TextColor.RED.value);
        assertEquals(39, TextColor.RED.getDefault());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (TextColor color : TextColor.values()) {
            assertSame(color, TextColor.valueOf(color.name()));
        }
    }
}
