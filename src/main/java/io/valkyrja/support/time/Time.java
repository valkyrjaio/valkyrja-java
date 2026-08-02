/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
