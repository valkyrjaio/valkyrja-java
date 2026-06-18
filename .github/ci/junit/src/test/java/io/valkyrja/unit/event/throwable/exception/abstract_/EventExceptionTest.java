/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.event.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.event.throwable.exception.abstract_.EventInvalidArgumentException;
import io.valkyrja.event.throwable.exception.abstract_.EventRuntimeException;
import org.junit.jupiter.api.Test;

/**
 * The event throwables are abstract with no concrete subclass yet (the concrete event exceptions
 * are not ported); their constructors are exercised via anonymous subclasses.
 */
final class EventExceptionTest {

    @Test
    void runtimeException() {
        var exception = new EventRuntimeException("boom") {};
        assertEquals("boom", exception.getMessage());

        var cause = new RuntimeException("root cause");
        var withCause = new EventRuntimeException("boom", cause) {};
        assertEquals("boom", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    @Test
    void invalidArgumentException() {
        var exception = new EventInvalidArgumentException("bad") {};
        assertEquals("bad", exception.getMessage());

        var cause = new RuntimeException("root cause");
        var withCause = new EventInvalidArgumentException("bad", cause) {};
        assertEquals("bad", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }
}