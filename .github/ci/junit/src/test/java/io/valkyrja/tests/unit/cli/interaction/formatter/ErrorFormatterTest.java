/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.ErrorFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link ErrorFormatter}. */
final class ErrorFormatterTest {

    @Test
    void usesLightWhiteOnRed() {
        assertEquals(2, new ErrorFormatter().getFormats().size());
        assertEquals("\033[97;41mx\033[39;49m", new ErrorFormatter().formatText("x"));
    }
}
