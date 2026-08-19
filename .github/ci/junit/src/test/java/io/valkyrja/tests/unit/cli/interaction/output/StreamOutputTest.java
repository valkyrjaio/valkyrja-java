/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.SuccessMessage;
import io.valkyrja.cli.interaction.output.StreamOutput;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionStreamWriteException;
import java.io.ByteArrayOutputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Test the {@link StreamOutput}. */
final class StreamOutputTest {

    private static final String ESCAPE = "\033";

    @Test
    void exposesAndReplacesStream() {
        var stream = new ByteArrayOutputStream();
        var output = new StreamOutput(stream);

        assertSame(stream, output.getStream());

        var other = new ByteArrayOutputStream();
        assertSame(other, ((StreamOutput) output.withStream(other)).getStream());
    }

    @Test
    void writesTheFormattedTextToTheStream() {
        var stream = new ByteArrayOutputStream();
        var output = new StreamOutput(stream);

        assertTrue(
                ((StreamOutput) output.writeMessage(new SuccessMessage("hello")))
                        .hasWrittenMessage());
        assertEquals(
                "%s[97;42mhello%s[39;49m".formatted(ESCAPE, ESCAPE),
                stream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void appendsEachMessageToTheStream() {
        var stream = new ByteArrayOutputStream();
        var output = new StreamOutput(stream);

        output.writeMessage(new Message("first"));
        output.writeMessage(new Message("second"));

        assertEquals("firstsecond", stream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void throwsWhenTheStreamIsUnwritable() {
        var output = new StreamOutput(new PipedOutputStream());

        assertThrows(
                CliInteractionStreamWriteException.class,
                () -> output.writeMessage(new Message("hello")));
    }
}
