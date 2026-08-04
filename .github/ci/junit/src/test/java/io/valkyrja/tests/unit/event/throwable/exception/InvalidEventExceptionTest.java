/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.event.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.event.throwable.exception.InvalidEventException;
import org.junit.jupiter.api.Test;

/** Test the {@link InvalidEventException}. */
final class InvalidEventExceptionTest {

    @Test
    void message() {
        String id = InvalidEventExceptionTest.class.getName();

        var exception = new InvalidEventException(id);

        assertEquals("Service with `" + id + "` is not an event", exception.getMessage());
    }
}
