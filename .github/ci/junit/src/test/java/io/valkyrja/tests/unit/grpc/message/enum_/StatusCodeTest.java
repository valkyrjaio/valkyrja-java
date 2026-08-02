/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.message.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.throwable.exception.GrpcInvalidStatusCodeException;
import org.junit.jupiter.api.Test;

/** Test the {@link StatusCode} enum. */
final class StatusCodeTest {

    @Test
    void value() {
        assertEquals(0, StatusCode.OK.getValue());
        assertEquals(5, StatusCode.NOT_FOUND.getValue());
        assertEquals(16, StatusCode.UNAUTHENTICATED.getValue());
    }

    @Test
    void defaultMessageIsNeverEmpty() {
        for (StatusCode code : StatusCode.values()) {
            assertFalse(code.getDefaultMessage().isEmpty());
        }
    }

    @Test
    void isOk() {
        assertTrue(StatusCode.OK.isOk());
        assertFalse(StatusCode.CANCELLED.isOk());
    }

    @Test
    void isCancellation() {
        assertTrue(StatusCode.CANCELLED.isCancellation());
        assertTrue(StatusCode.DEADLINE_EXCEEDED.isCancellation());
        assertFalse(StatusCode.OK.isCancellation());
        assertFalse(StatusCode.NOT_FOUND.isCancellation());
    }

    @Test
    void fromValueResolvesEachConstant() {
        for (StatusCode code : StatusCode.values()) {
            assertSame(code, StatusCode.fromValue(code.getValue()));
        }
    }

    @Test
    void fromValueThrowsForUnknownValue() {
        var exception =
                assertThrows(GrpcInvalidStatusCodeException.class, () -> StatusCode.fromValue(99));

        // The module exception still extends the language root, so a caller catching
        // IllegalArgumentException around fromValue keeps working.
        assertInstanceOf(IllegalArgumentException.class, exception);
    }

    @Test
    void valuesAreUniqueAndContiguous() {
        StatusCode[] values = StatusCode.values();
        assertEquals(17, values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].getValue());
        }
    }
}
