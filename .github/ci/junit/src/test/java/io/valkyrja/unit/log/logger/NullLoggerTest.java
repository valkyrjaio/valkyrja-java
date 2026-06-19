/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.log.logger;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.log.enum_.LogLevel;
import io.valkyrja.log.logger.NullLogger;
import io.valkyrja.log.throwable.exception.LogInvalidLogLevelException;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test {@link NullLogger} and the {@link io.valkyrja.log.logger.abstract_.Logger} dispatcher. */
final class NullLoggerTest {

    private final NullLogger logger = new NullLogger();

    @Test
    void logDispatchesEveryLevelWithoutError() {
        for (LogLevel level : LogLevel.values()) {
            logger.log(level, "message", Map.of());
        }
    }

    @Test
    void directLevelMethodsAreNoOps() {
        logger.debug("m", Map.of());
        logger.info("m", Map.of());
        logger.notice("m", Map.of());
        logger.warning("m", Map.of());
        logger.error("m", Map.of());
        logger.critical("m", Map.of());
        logger.alert("m", Map.of());
        logger.emergency("m", Map.of());
        logger.throwable(new RuntimeException("boom"), "m", Map.of());
    }

    @Test
    void logWithNullLevelThrows() {
        assertThrows(
                LogInvalidLogLevelException.class, () -> logger.log(null, "message", Map.of()));
    }
}
