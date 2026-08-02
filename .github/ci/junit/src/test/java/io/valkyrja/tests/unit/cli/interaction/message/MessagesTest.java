/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.Messages;
import org.junit.jupiter.api.Test;

/** Test the {@link Messages}. */
final class MessagesTest {

    @Test
    void concatenatesTextAndFormattedText() {
        var messages = new Messages(new Message("a"), new Message("b"), new Message("c"));

        assertEquals("abc", messages.getText());
        assertEquals("abc", messages.getFormattedText());
    }
}
