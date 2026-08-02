/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Test the {@link EmptyOutput}. */
final class EmptyOutputTest {

    @Test
    void writesNothing() {
        var original = System.out;
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            new EmptyOutput().writeMessage(new Message("x"));
            new EmptyOutput(true, false, false, ExitCode.SUCCESS).writeMessage(new Message("y"));
        } finally {
            System.setOut(original);
        }

        assertEquals("", buffer.toString(StandardCharsets.UTF_8));
    }
}
