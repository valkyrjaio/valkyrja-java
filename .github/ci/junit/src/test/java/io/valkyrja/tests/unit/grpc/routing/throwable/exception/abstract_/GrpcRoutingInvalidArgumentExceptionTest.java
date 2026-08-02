/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.routing.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.grpc.routing.throwable.exception.abstract_.GrpcRoutingInvalidArgumentException;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcInvalidArgumentException;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcRoutingInvalidArgumentException}. */
final class GrpcRoutingInvalidArgumentExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new GrpcRoutingInvalidArgumentException("message") {};

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new GrpcRoutingInvalidArgumentException("message", cause) {};

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void isCatchableAsTheComponentCategorical() {
        var exception = new GrpcRoutingInvalidArgumentException("message") {};

        assertInstanceOf(GrpcInvalidArgumentException.class, exception);
    }
}
