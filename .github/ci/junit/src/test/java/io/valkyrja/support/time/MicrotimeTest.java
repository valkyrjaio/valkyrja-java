/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.support.time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MicrotimeTest {

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
