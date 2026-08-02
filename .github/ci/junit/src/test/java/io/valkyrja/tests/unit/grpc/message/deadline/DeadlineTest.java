/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.message.deadline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.deadline.Deadline;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/** Test the {@link Deadline} value type. */
final class DeadlineTest {

    private static final Instant NOW = Instant.parse("2026-07-15T00:00:00Z");

    private static Clock fixedAt(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    @Test
    void fromTimeoutComputesAbsoluteTime() {
        Deadline deadline = Deadline.fromTimeout(Duration.ofSeconds(5), fixedAt(NOW));
        assertTrue(deadline.hasDeadline());
        assertEquals(NOW.plusSeconds(5), deadline.getAbsoluteTime());
    }

    @Test
    void remainingIsPositiveBeforeExpiry() {
        Deadline deadline = Deadline.fromAbsolute(NOW.plusSeconds(10), fixedAt(NOW));
        assertEquals(Duration.ofSeconds(10), deadline.getRemaining());
        assertFalse(deadline.isExpired());
    }

    @Test
    void remainingIsZeroAfterExpiry() {
        Deadline deadline = Deadline.fromAbsolute(NOW.minusSeconds(1), fixedAt(NOW));
        assertEquals(Duration.ZERO, deadline.getRemaining());
        assertTrue(deadline.isExpired());
    }

    @Test
    void expiredExactlyAtDeadline() {
        Deadline deadline = Deadline.fromAbsolute(NOW, fixedAt(NOW));
        assertTrue(deadline.isExpired());
    }

    @Test
    void noneNeverExpiresAndHasNoDeadline() {
        Deadline deadline = Deadline.none(fixedAt(NOW));
        assertFalse(deadline.hasDeadline());
        assertFalse(deadline.isExpired());
        assertEquals(Instant.MAX, deadline.getAbsoluteTime());
        assertTrue(deadline.getRemaining().compareTo(Duration.ofDays(365)) > 0);
    }

    @Test
    void systemClockFactoriesProduceDeadlines() {
        assertTrue(Deadline.fromTimeout(Duration.ofSeconds(1)).hasDeadline());
        assertTrue(Deadline.fromAbsolute(Instant.now().plusSeconds(1)).hasDeadline());
        assertFalse(Deadline.none().hasDeadline());
    }
}
