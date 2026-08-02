/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.TextColorFormat;
import org.junit.jupiter.api.Test;

/** Test the {@link TextColorFormat}. */
final class TextColorFormatTest {

    @Test
    void usesColorCodeAndDefaultReset() {
        var format = new TextColorFormat(TextColor.RED);

        assertEquals("31", format.getSetCode());
        assertEquals("39", format.getUnsetCode());
    }
}
