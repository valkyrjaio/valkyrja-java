/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.support.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.support.time.Time;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Test the {@link Time} utility. */
final class TimeTest {

    @AfterEach
    void tearDown() {
        Time.unfreeze();
    }

    @Test
    void getReturnsCurrentSeconds() {
        double before = System.nanoTime() / 1_000_000_000.0;
        double result = Time.get();
        double after = System.nanoTime() / 1_000_000_000.0;

        assertTrue(result >= before && result <= after);
    }

    @Test
    void freezeFreezesTime() {
        Time.freeze();

        assertEquals(Time.get(), Time.get());
    }

    @Test
    void unfreezeResumesTime() throws InterruptedException {
        Time.freeze();
        double frozen = Time.get();

        Time.unfreeze();
        Thread.sleep(1);

        assertNotEquals(frozen, Time.get());
    }
}
