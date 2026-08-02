/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetUnsetException;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpHeaderUnsupportedOffsetUnsetException}. */
final class HttpHeaderUnsupportedOffsetUnsetExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new HttpHeaderUnsupportedOffsetUnsetException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new HttpHeaderUnsupportedOffsetUnsetException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
