/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.dispatch.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.dispatch.throwable.exception.DispatchInvalidDispatchCapabilityException;
import io.valkyrja.dispatch.throwable.exception.DispatchNoClassException;
import org.junit.jupiter.api.Test;

/** Test the dispatch throwables (covering their abstract argument/runtime bases). */
final class DispatchExceptionTest {

    @Test
    void invalidDispatchCapabilityMessageConstructor() {
        var exception = new DispatchInvalidDispatchCapabilityException("message");

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void invalidDispatchCapabilityMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new DispatchInvalidDispatchCapabilityException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void noClassMessageConstructor() {
        var exception = new DispatchNoClassException("message");

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void noClassMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new DispatchNoClassException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}