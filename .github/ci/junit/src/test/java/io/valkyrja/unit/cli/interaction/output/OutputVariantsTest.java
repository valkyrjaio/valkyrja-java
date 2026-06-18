/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.FileOutput;
import io.valkyrja.cli.interaction.output.PlainOutput;
import io.valkyrja.cli.interaction.output.StreamOutput;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Test the {@link io.valkyrja.cli.interaction.output.Output} subclasses. */
final class OutputVariantsTest {

    @Test
    void emptyOutputWritesNothing() {
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

    @Test
    void plainOutputWritesUnformattedText() {
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

    @Test
    void streamOutputExposesAndReplacesStream() {
        var stream = new ByteArrayOutputStream();
        var output = new StreamOutput(stream);

        assertSame(stream, output.getStream());

        var other = new ByteArrayOutputStream();
        assertSame(other, ((StreamOutput) output.withStream(other)).getStream());
        // outputMessage is a no-op for StreamOutput; writing still records the message.
        assertTrue(((StreamOutput) output.writeMessage(new Message("x"))).hasWrittenMessage());
    }

    @Test
    void fileOutputExposesAndReplacesFilepath() {
        var output = new FileOutput("/tmp/out.txt");

        assertEquals("/tmp/out.txt", output.getFilepath());
        assertEquals("/tmp/other.txt", ((FileOutput) output.withFilepath("/tmp/other.txt")).getFilepath());
        assertTrue(((FileOutput) output.writeMessage(new Message("x"))).hasWrittenMessage());
    }
}