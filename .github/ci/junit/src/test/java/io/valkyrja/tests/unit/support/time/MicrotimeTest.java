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

import io.valkyrja.support.time.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public final class MicrotimeTest {

    @AfterEach
    void tearDown() {
        Microtime.unfreeze();
    }

    @Test
    void get_returnsMicroseconds() {
        long before = System.nanoTime() / 1000;
        long result = Microtime.get();
        long after = System.nanoTime() / 1000;

        assert result >= before && result <= after;
    }

    @Test
    void freeze_freezesTime() {
        Microtime.freeze();
        long first = Microtime.get();
        long second = Microtime.get();

        assertEquals(first, second);
    }

    @Test
    void unfreeze_resumesTime() throws InterruptedException {
        Microtime.freeze();
        long frozen = Microtime.get();

        Microtime.unfreeze();
        Thread.sleep(1);
        long after = Microtime.get();

        assertNotEquals(frozen, after);
    }
}
