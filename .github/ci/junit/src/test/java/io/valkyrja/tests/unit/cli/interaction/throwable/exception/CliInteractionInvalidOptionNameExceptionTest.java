/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidOptionNameException;
import org.junit.jupiter.api.Test;

/** Test the {@link CliInteractionInvalidOptionNameException}. */
final class CliInteractionInvalidOptionNameExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new CliInteractionInvalidOptionNameException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new CliInteractionInvalidOptionNameException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
