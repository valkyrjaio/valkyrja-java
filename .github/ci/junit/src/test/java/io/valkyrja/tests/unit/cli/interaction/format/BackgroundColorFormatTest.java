/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import io.valkyrja.cli.interaction.format.BackgroundColorFormat;
import org.junit.jupiter.api.Test;

/** Test the {@link BackgroundColorFormat}. */
final class BackgroundColorFormatTest {

    @Test
    void usesColorCodeAndDefaultReset() {
        var format = new BackgroundColorFormat(BackgroundColor.RED);

        assertEquals("41", format.getSetCode());
        assertEquals("49", format.getUnsetCode());
    }
}
