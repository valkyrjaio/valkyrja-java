/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.message.Answer;
import io.valkyrja.cli.interaction.message.Question;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Test the {@link Question} message. */
final class QuestionTest {

    private static final BiFunction<OutputContract, AnswerContract, OutputContract> CALLABLE =
            (output, answer) -> output;

    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreStdin() {
        System.setIn(originalIn);
    }

    private static Question question() {
        return new Question("Continue?", CALLABLE, new Answer("yes"));
    }

    @Test
    void accessorsAndCopies() {
        var question = question();
        var answer = new Answer("no");

        assertSame(CALLABLE, question.getCallable());
        assertEquals("yes", question.getAnswer().getDefaultResponse());

        BiFunction<OutputContract, AnswerContract, OutputContract> other = (o, a) -> o;
        assertNotSame(question, question.withCallable(other));
        assertSame(other, question.withCallable(other).getCallable());
        assertSame(answer, question.withAnswer(answer).getAnswer());
    }

    @Test
    void askReturnsAnswerWithUserResponse() {
        System.setIn(new ByteArrayInputStream("maybe\n".getBytes(StandardCharsets.UTF_8)));

        var answered = question().ask();

        assertEquals("maybe", answered.getUserResponse());
        assertEquals(true, answered.hasBeenAnswered());
    }

    @Test
    void askReturnsDefaultAnswerOnEmptyInput() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        var answered = question().ask();

        assertEquals("yes", answered.getUserResponse());
        assertEquals(false, answered.hasBeenAnswered());
    }

    @Test
    void askReturnsDefaultAnswerOnIoError() {
        System.setIn(
                new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("boom");
                    }
                });

        var answered = question().ask();

        assertEquals("yes", answered.getUserResponse());
    }
}