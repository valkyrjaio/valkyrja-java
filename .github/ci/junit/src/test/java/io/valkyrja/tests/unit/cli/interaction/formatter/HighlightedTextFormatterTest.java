/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.HighlightedTextFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link HighlightedTextFormatter}. */
final class HighlightedTextFormatterTest {

    @Test
    void usesYellowText() {
        assertEquals("\033[33mx\033[39m", new HighlightedTextFormatter().formatText("x"));
    }
}
