/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.event.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.event.throwable.exception.EventInvalidEventException;
import org.junit.jupiter.api.Test;

/** Test the {@link EventInvalidEventException}. */
final class EventInvalidEventExceptionTest {

    @Test
    void message() {
        String id = EventInvalidEventExceptionTest.class.getName();

        var exception = new EventInvalidEventException(id);

        assertEquals("Service with `" + id + "` is not an event", exception.getMessage());
    }
}
