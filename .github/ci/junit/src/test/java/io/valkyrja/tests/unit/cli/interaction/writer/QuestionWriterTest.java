/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.writer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Answer;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.Question;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionExpectedQuestionOutputException;
import io.valkyrja.cli.interaction.writer.QuestionWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Test the {@link QuestionWriter}. */
final class QuestionWriterTest {

    private static final BiFunction<OutputContract, AnswerContract, OutputContract> CALLABLE =
            (output, answer) -> output;

    private final QuestionWriter writer = new QuestionWriter();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private static void muteStdout() {
        System.setOut(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    }

    private static Question question() {
        return new Question("Continue?", CALLABLE, new Answer("yes"));
    }

    @Test
    void shouldWriteMessageOnlyForQuestions() {
        assertTrue(writer.shouldWriteMessage(question()));
        assertFalse(writer.shouldWriteMessage(new Message("x")));
    }

    @Test
    void writeRejectsNonQuestionMessages() {
        assertThrows(
                CliInteractionExpectedQuestionOutputException.class,
                () -> writer.write(new Output(), new Message("x")));
    }

    @Test
    void writeNonInteractiveUsesDefaultAnswer() {
        muteStdout();
        var output = new Output().withIsInteractive(false);

        assertNotNull(writer.write(output, question()));
    }

    @Test
    void writeInteractiveWithValidResponse() {
        muteStdout();
        System.setIn(new ByteArrayInputStream("yes\n".getBytes(StandardCharsets.UTF_8)));

        assertNotNull(writer.write(new Output(), question()));
    }

    @Test
    void writeInteractiveWithInvalidResponseRetries() {
        muteStdout();
        // First read is an invalid response, forcing a retry; the retry reads from the now-closed
        // stream and falls back to the (valid) default answer, terminating the recursion.
        System.setIn(new ByteArrayInputStream("nope\n".getBytes(StandardCharsets.UTF_8)));

        assertNotNull(writer.write(new Output(), question()));
    }

    @Test
    void writeWithQuietOutputSkipsInteractivePrompt() {
        muteStdout();

        assertNotNull(writer.write(new Output().withIsQuiet(true), question()));
    }

    @Test
    void writeWithSilentOutputSkipsInteractivePrompt() {
        muteStdout();

        assertNotNull(writer.write(new Output().withIsSilent(true), question()));
    }
}
