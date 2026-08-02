/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.PlainOutput;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Test the {@link PlainOutput}. */
final class PlainOutputTest {

    @Test
    void writesUnformattedText() {
        var original = System.out;
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            new PlainOutput().writeMessage(new Message("plain"));
        } finally {
            System.setOut(original);
        }

        assertEquals("plain", buffer.toString(StandardCharsets.UTF_8));
    }
}
