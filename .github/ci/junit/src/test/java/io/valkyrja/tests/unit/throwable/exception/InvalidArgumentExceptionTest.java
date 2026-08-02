/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.throwable.exception.InvalidArgumentException;
import org.junit.jupiter.api.Test;

/** Test the base {@link InvalidArgumentException}. */
final class InvalidArgumentExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new InvalidArgumentException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new InvalidArgumentException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void throwExceptionThrows() {
        var thrown =
                assertThrows(
                        InvalidArgumentException.class,
                        () -> InvalidArgumentException.throwException("boom"));

        assertEquals("boom", thrown.getMessage());
    }

    @Test
    void getTraceCodeReturnsMd5Hex() {
        assertTrue(new InvalidArgumentException("message").getTraceCode().matches("[0-9a-f]{32}"));
    }
}
