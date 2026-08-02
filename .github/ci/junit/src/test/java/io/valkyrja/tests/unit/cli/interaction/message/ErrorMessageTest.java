/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.message.ErrorMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link ErrorMessage}. */
final class ErrorMessageTest {

    @Test
    void appliesErrorFormatter() {
        var message = new ErrorMessage("err");

        assertEquals("err", message.getText());
        assertEquals("\033[97;41merr\033[39;49m", message.getFormattedText());
    }
}
