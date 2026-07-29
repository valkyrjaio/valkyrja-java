/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
