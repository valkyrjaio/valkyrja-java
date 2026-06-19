/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.reflection.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.reflection.throwable.exception.ReflectionInvalidClassToInstantiateException;
import io.valkyrja.reflection.throwable.exception.abstract_.ReflectionInvalidArgumentException;
import org.junit.jupiter.api.Test;

/** Test the reflection throwables, including the abstract argument-exception base. */
final class ReflectionExceptionTest {

    @Test
    void invalidClassToInstantiateMessageConstructor() {
        var exception = new ReflectionInvalidClassToInstantiateException("message");

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void invalidClassToInstantiateMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new ReflectionInvalidClassToInstantiateException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void invalidArgumentMessageConstructor() {
        var exception = new ReflectionInvalidArgumentException("message") {};

        assertEquals("message", exception.getMessage());
        assertTrue(exception.getTraceCode().matches("[0-9a-f]{32}"));
    }

    @Test
    void invalidArgumentMessageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new ReflectionInvalidArgumentException("message", cause) {};

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
