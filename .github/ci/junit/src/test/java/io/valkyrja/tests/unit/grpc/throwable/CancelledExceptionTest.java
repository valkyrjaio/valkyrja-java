/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import org.junit.jupiter.api.Test;

/** Test the {@link CancelledException}. */
final class CancelledExceptionTest {

    @Test
    void carriesMessageAndReason() {
        CancelledException ex =
                new CancelledException("stop", CancellationReason.DEADLINE_EXCEEDED);
        assertEquals("stop", ex.getMessage());
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, ex.getReason());
    }

    @Test
    void reasonIsNullWhenUnspecified() {
        CancelledException ex = new CancelledException("stop");
        assertEquals("stop", ex.getMessage());
        assertNull(ex.getReason());
    }
}
