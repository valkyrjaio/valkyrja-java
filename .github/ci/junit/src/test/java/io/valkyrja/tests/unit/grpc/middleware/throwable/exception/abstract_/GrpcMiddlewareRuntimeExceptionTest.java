/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.middleware.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.grpc.middleware.throwable.exception.abstract_.GrpcMiddlewareRuntimeException;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcMiddlewareRuntimeException}. */
final class GrpcMiddlewareRuntimeExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new GrpcMiddlewareRuntimeException("message") {};

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new GrpcMiddlewareRuntimeException("message", cause) {};

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void isCatchableAsTheComponentCategorical() {
        var exception = new GrpcMiddlewareRuntimeException("message") {};

        assertInstanceOf(GrpcRuntimeException.class, exception);
    }
}
