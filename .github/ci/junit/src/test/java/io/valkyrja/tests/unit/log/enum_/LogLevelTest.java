/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.log.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.log.enum_.LogLevel;
import org.junit.jupiter.api.Test;

/** Test the {@link LogLevel} enum. */
final class LogLevelTest {

    @Test
    void valuesMapToLowercaseStrings() {
        assertEquals("debug", LogLevel.DEBUG.getValue());
        assertEquals("info", LogLevel.INFO.getValue());
        assertEquals("notice", LogLevel.NOTICE.getValue());
        assertEquals("warning", LogLevel.WARNING.getValue());
        assertEquals("error", LogLevel.ERROR.getValue());
        assertEquals("critical", LogLevel.CRITICAL.getValue());
        assertEquals("alert", LogLevel.ALERT.getValue());
        assertEquals("emergency", LogLevel.EMERGENCY.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (LogLevel level : LogLevel.values()) {
            assertSame(level, LogLevel.valueOf(level.name()));
        }
    }
}
