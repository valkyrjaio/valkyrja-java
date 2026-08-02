/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.dispatch.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.dispatch.throwable.exception.abstract_.DispatchRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the {@link DispatchRuntimeException}. */
final class DispatchRuntimeExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new DispatchRuntimeException("message") {};

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new DispatchRuntimeException("message", cause) {};

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
