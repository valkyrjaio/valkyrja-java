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
import io.valkyrja.cli.interaction.output.StreamOutput;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionUnwritableStreamException;
import java.io.ByteArrayOutputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Test the {@link StreamOutput}. */
final class StreamOutputTest {

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
        var message = new Message("hello");
        var output = new StreamOutput(stream);

        assertTrue(((StreamOutput) output.writeMessage(message)).hasWrittenMessage());
        assertEquals(message.getFormattedText(), stream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void appendsEachMessageToTheStream() {
        var stream = new ByteArrayOutputStream();
        var first = new Message("first");
        var second = new Message("second");
        var output = new StreamOutput(stream);

        output.writeMessage(first);
        output.writeMessage(second);

        assertEquals(
                first.getFormattedText() + second.getFormattedText(),
                stream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void throwsWhenTheStreamIsUnwritable() {
        var output = new StreamOutput(new PipedOutputStream());

        assertThrows(
                CliInteractionUnwritableStreamException.class,
                () -> output.writeMessage(new Message("hello")));
    }
}
