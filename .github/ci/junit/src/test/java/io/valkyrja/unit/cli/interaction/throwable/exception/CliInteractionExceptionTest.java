/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.throwable.exception.CliInteractionExpectedQuestionOutputException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidNonEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidOptionNameException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoFormatterException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoValidationCallableException;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the cli interaction throwables and their abstract argument/runtime bases. */
final class CliInteractionExceptionTest {

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> factories =
            List.of(
                    CliInteractionExpectedQuestionOutputException::new,
                    CliInteractionInvalidEmptyValueException::new,
                    CliInteractionInvalidNonEmptyValueException::new,
                    CliInteractionInvalidOptionNameException::new,
                    CliInteractionNoFormatterException::new,
                    CliInteractionNoValidationCallableException::new);

    @Test
    void messageConstructors() {
        assertEquals("m", new CliInteractionExpectedQuestionOutputException("m").getMessage());
        assertEquals("m", new CliInteractionInvalidEmptyValueException("m").getMessage());
        assertEquals("m", new CliInteractionInvalidNonEmptyValueException("m").getMessage());
        assertEquals("m", new CliInteractionInvalidOptionNameException("m").getMessage());
        assertEquals("m", new CliInteractionNoFormatterException("m").getMessage());
        assertEquals("m", new CliInteractionNoValidationCallableException("m").getMessage());
    }

    @Test
    void messageAndCauseConstructors() {
        var cause = new IllegalStateException("cause");

        for (var factory : factories) {
            var exception = factory.apply("m", cause);
            assertEquals("m", exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }

    @Test
    void traceCodeIsAvailable() {
        assertTrue(new CliInteractionNoFormatterException("m").getTraceCode().matches("[0-9a-f]{32}"));
    }
}
