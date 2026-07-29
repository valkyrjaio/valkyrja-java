/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.writer.QuestionWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the base {@link Output}. */
final class OutputTest {

    private static String captureStdout(Supplier<?> action) {
        var original = System.out;
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.get();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void defaults() {
        var output = new Output();

        assertTrue(output.isInteractive());
        assertFalse(output.isQuiet());
        assertFalse(output.isSilent());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertTrue(output.getMessages().isEmpty());
    }

    @Test
    void messageQueues() {
        var output = new Output(true, false, false, ExitCode.SUCCESS, new Message("a"));

        assertTrue(output.hasUnwrittenMessage());
        assertFalse(output.hasWrittenMessage());
        assertEquals(1, output.getUnwrittenMessages().size());
        assertEquals(1, output.getMessages().size());

        var replaced = output.withMessages(new Message("b"), new Message("c"));
        assertEquals(2, replaced.getUnwrittenMessages().size());

        var added = output.withAddedMessages(new Message("d")).withAddedMessage(new Message("e"));
        assertEquals(3, added.getUnwrittenMessages().size());
    }

    @Test
    void writeMessagesMovesUnwrittenToWritten() {
        var output = new Output(true, false, false, ExitCode.SUCCESS, new Message("x"));

        var written = output.writeMessages();

        assertTrue(written.hasWrittenMessage());
        assertFalse(written.hasUnwrittenMessage());
    }

    @Test
    void writeMessageWritesSingle() {
        var written = new Output().writeMessage(new Message("x"));

        assertEquals(1, written.getWrittenMessages().size());
    }

    @Test
    void flagsAndExitCodeMutations() {
        var output =
                new Output()
                        .withIsInteractive(false)
                        .withIsQuiet(true)
                        .withIsSilent(true)
                        .withExitCode(ExitCode.ERROR);

        assertFalse(output.isInteractive());
        assertTrue(output.isQuiet());
        assertTrue(output.isSilent());
        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertEquals(7, output.withExitCode(7).getExitCode());
    }

    @Test
    void writers() {
        var output = new Output().withWriters(new QuestionWriter());

        assertEquals(1, output.getWriters().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> output.getWriters().add(new QuestionWriter()));
    }

    @Test
    void printsToStdoutWhenNotSilentOrQuiet() {
        var output = new Output();

        var printed = captureStdout(() -> output.writeMessage(new Message("hello")));

        assertEquals("hello", printed);
    }

    @Test
    void silentOutputDoesNotPrint() {
        var output = new Output(true, false, true, ExitCode.SUCCESS);

        assertEquals("", captureStdout(() -> output.writeMessage(new Message("hi"))));
    }

    @Test
    void quietSuccessfulOutputDoesNotPrint() {
        var output = new Output(true, true, false, ExitCode.SUCCESS);

        assertEquals("", captureStdout(() -> output.writeMessage(new Message("hi"))));
    }

    @Test
    void quietNonSuccessfulOutputPrints() {
        var output = new Output(true, true, false, ExitCode.ERROR);

        assertEquals("hi", captureStdout(() -> output.writeMessage(new Message("hi"))));
    }
}
