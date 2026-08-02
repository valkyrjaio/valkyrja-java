/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.message.NewLine;
import org.junit.jupiter.api.Test;

/** Test the {@link NewLine}. */
final class NewLineTest {

    @Test
    void isANewlineCharacter() {
        assertEquals("\n", new NewLine().getText());
        assertEquals("\n", new NewLine(null).getText());
    }
}
