/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.log.logger.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.log.enum_.LogLevel;
import io.valkyrja.log.logger.abstract_.Logger;
import io.valkyrja.log.throwable.exception.LogInvalidLogLevelException;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link Logger}. */
final class LoggerTest {

    private static final class RecordingLogger extends Logger {
        String last = "";

        @Override
        public void debug(String message, Map<String, Object> context) {
            last = "debug";
        }

        @Override
        public void info(String message, Map<String, Object> context) {
            last = "info";
        }

        @Override
        public void notice(String message, Map<String, Object> context) {
            last = "notice";
        }

        @Override
        public void warning(String message, Map<String, Object> context) {
            last = "warning";
        }

        @Override
        public void error(String message, Map<String, Object> context) {
            last = "error";
        }

        @Override
        public void critical(String message, Map<String, Object> context) {
            last = "critical";
        }

        @Override
        public void alert(String message, Map<String, Object> context) {
            last = "alert";
        }

        @Override
        public void emergency(String message, Map<String, Object> context) {
            last = "emergency";
        }

        @Override
        public void throwable(Throwable throwable, String message, Map<String, Object> context) {
            last = "throwable";
        }
    }

    @Test
    void dispatchesEachLevelToTheMatchingMethod() {
        var logger = new RecordingLogger();

        for (LogLevel level : LogLevel.values()) {
            logger.log(level, "m", Map.of());

            assertEquals(level.name().toLowerCase(Locale.ROOT), logger.last);
        }
    }

    @Test
    void rejectsNullLevel() {
        assertThrows(
                LogInvalidLogLevelException.class,
                () -> new RecordingLogger().log(null, "m", Map.of()));
    }
}
