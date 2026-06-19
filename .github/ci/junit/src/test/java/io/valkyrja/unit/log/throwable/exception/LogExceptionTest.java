/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.log.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.log.throwable.exception.LogInvalidLogLevelException;
import io.valkyrja.log.throwable.exception.abstract_.LogRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the log throwables, including the abstract runtime-exception base. */
final class LogExceptionTest {

    @Test
    void invalidLogLevelMessageConstructor() {
        var exception = new LogInvalidLogLevelException("message");

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void invalidLogLevelMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new LogInvalidLogLevelException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void runtimeMessageConstructor() {
        var exception = new LogRuntimeException("message") {};

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void runtimeMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new LogRuntimeException("message", cause) {};

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
