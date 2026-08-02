/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.message.WarningMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link WarningMessage}. */
final class WarningMessageTest {

    @Test
    void appliesWarningFormatter() {
        assertEquals("\033[30;43mwarn\033[39;49m", new WarningMessage("warn").getFormattedText());
    }
}
