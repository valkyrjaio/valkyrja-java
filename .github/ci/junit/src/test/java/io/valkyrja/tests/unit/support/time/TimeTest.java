/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
