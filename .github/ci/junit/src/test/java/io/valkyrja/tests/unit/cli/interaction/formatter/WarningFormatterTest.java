/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.WarningFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link WarningFormatter}. */
final class WarningFormatterTest {

    @Test
    void usesBlackOnYellow() {
        assertEquals("\033[30;43mx\033[39;49m", new WarningFormatter().formatText("x"));
    }
}
