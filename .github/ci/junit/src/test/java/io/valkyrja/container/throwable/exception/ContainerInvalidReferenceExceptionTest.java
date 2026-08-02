/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

final class ContainerInvalidReferenceExceptionTest {

    @Test
    void message() {
        String id = ContainerInvalidReferenceExceptionTest.class.getName();

        var exception = new ContainerInvalidReferenceException(id);

        assertEquals("Service with `" + id + "` not found", exception.getMessage());
    }

    @Test
    void messageWithCause() {
        String id = ContainerInvalidReferenceExceptionTest.class.getName();
        var cause = new RuntimeException("root cause");

        var exception = new ContainerInvalidReferenceException(id, cause);

        assertEquals("Service with `" + id + "` not found", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
