/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.support.time;

import org.jspecify.annotations.Nullable;

public class Time {

    private Time() {
        /* This utility class should not be instantiated */
    }

    private static @Nullable Double frozenTime = null;

    public static void freeze() {
        frozenTime = microtime();
    }

    public static void unfreeze() {
        frozenTime = null;
    }

    public static Double get() {
        if (frozenTime != null) {
            return frozenTime;
        }

        return microtime();
    }

    protected static Double microtime() {
        return System.nanoTime() / 1_000_000_000.0;
    }
}
