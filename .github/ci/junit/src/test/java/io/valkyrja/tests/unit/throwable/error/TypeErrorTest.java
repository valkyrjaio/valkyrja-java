/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.throwable.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.throwable.error.TypeError;
import org.junit.jupiter.api.Test;

/** Test the {@link TypeError}. */
final class TypeErrorTest {

    @Test
    void messageConstructor() {
        var error = new TypeError("message");

        assertEquals("message", error.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var error = new TypeError("message", cause);

        assertEquals("message", error.getMessage());
        assertSame(cause, error.getCause());
    }

    @Test
    void getTraceCodeReturnsMd5Hex() {
        assertTrue(new TypeError("message").getTraceCode().matches("[0-9a-f]{32}"));
    }
}
