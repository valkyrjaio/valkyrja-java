/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.routing.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcRoutingInvalidMethodException}. */
final class GrpcRoutingInvalidMethodExceptionTest {

    @Test
    void messageConstructor() {
        GrpcRoutingInvalidMethodException ex = new GrpcRoutingInvalidMethodException("bad");
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new IllegalStateException("root");
        GrpcRoutingInvalidMethodException ex = new GrpcRoutingInvalidMethodException("bad", cause);
        assertEquals("bad", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
