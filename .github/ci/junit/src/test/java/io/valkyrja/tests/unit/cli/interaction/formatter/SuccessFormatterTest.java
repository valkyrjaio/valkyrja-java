/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.SuccessFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link SuccessFormatter}. */
final class SuccessFormatterTest {

    @Test
    void usesLightWhiteOnGreen() {
        assertEquals("\033[97;42mx\033[39;49m", new SuccessFormatter().formatText("x"));
    }
}
