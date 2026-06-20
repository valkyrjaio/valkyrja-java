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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Answer;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoValidationCallableException;
import org.junit.jupiter.api.Test;

/** Test the {@link Answer} message. */
final class AnswerTest {

    @Test
    void defaultConstructorSeedsAllowedAndUserResponse() {
        var answer = new Answer("yes");

        assertEquals("yes", answer.getDefaultResponse());
        assertEquals("yes", answer.getUserResponse());
        assertEquals("You answered: `yes`", answer.getText());
        assertTrue(answer.getAllowedResponses().contains("yes"));
        assertFalse(answer.hasValidationCallable());
        assertFalse(answer.hasBeenAnswered());
    }

    @Test
    void getValidationCallableThrowsWhenAbsent() {
        assertThrows(
                CliInteractionNoValidationCallableException.class,
                () -> new Answer("yes").getValidationCallable());
    }

    @Test
    void withUserResponseMarksAnswered() {
        var answer = new Answer("yes").withUserResponse("no");

        assertEquals("no", answer.getUserResponse());
        assertTrue(answer.hasBeenAnswered());
    }

    @Test
    void withDefaultResponseUpdatesUserResponseWhenNotAnswered() {
        var answer = new Answer("yes").withDefaultResponse("no");

        assertEquals("no", answer.getDefaultResponse());
        assertEquals("no", answer.getUserResponse());
        assertTrue(answer.getAllowedResponses().contains("no"));
        assertTrue(answer.getAllowedResponses().contains("yes"));
    }

    @Test
    void withDefaultResponseKeepsUserResponseWhenAlreadyAnswered() {
        var answer = new Answer("yes").withUserResponse("maybe").withDefaultResponse("no");

        assertEquals("no", answer.getDefaultResponse());
        assertEquals("maybe", answer.getUserResponse());
    }

    @Test
    void withDefaultResponseAlreadyAllowedDoesNotDuplicate() {
        var answer = new Answer("yes").withDefaultResponse("yes");

        assertEquals(1, answer.getAllowedResponses().stream().filter("yes"::equals).count());
    }

    @Test
    void withAllowedResponsesKeepsDefault() {
        var answer = new Answer("yes").withAllowedResponses("a", "b");

        assertTrue(answer.getAllowedResponses().contains("a"));
        assertTrue(answer.getAllowedResponses().contains("b"));
        assertTrue(answer.getAllowedResponses().contains("yes"));
    }

    @Test
    void validationCallableLifecycle() {
        var answer = new Answer("yes").withValidationCallable("ok"::equals);

        assertTrue(answer.hasValidationCallable());
        assertTrue(answer.getValidationCallable().test("ok"));
        assertFalse(answer.withoutValidationCallable().hasValidationCallable());
    }

    @Test
    void withHasBeenAnswered() {
        assertTrue(new Answer("yes").withHasBeenAnswered(true).hasBeenAnswered());
    }

    @Test
    void isValidResponseWhenResponseIsAllowed() {
        assertTrue(new Answer("yes").isValidResponse());
    }

    @Test
    void isValidResponseFailsForDisallowedResponseWithoutCallable() {
        assertFalse(new Answer("yes").withUserResponse("nope").isValidResponse());
    }

    @Test
    void isValidResponsePassesViaValidationCallable() {
        var answer =
                new Answer("yes").withUserResponse("nope").withValidationCallable("nope"::equals);

        assertTrue(answer.isValidResponse());
    }

    @Test
    void withAllowedResponsesDoesNotDuplicateDefault() {
        var answer = new Answer("yes").withAllowedResponses("yes", "no");

        assertEquals(
                1, answer.getAllowedResponses().stream().filter("yes"::equals).count());
    }

}
