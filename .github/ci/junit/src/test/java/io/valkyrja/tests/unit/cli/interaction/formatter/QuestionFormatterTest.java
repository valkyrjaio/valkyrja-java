/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.QuestionFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link QuestionFormatter}. */
final class QuestionFormatterTest {

    @Test
    void usesMagentaText() {
        assertEquals("\033[35mx\033[39m", new QuestionFormatter().formatText("x"));
    }
}
