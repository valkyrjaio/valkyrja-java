/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.request.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.request.throwable.exception.HttpRequestInvalidMethodException;
import io.valkyrja.http.message.request.throwable.exception.HttpRequestInvalidRequestTargetException;
import io.valkyrja.http.message.request.throwable.exception.abstract_.HttpRequestRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the http request throwables and their abstract argument base. */
final class HttpRequestExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void invalidMethod() {
        assertEquals("m", new HttpRequestInvalidMethodException("m").getMessage());
        assertSame(cause, new HttpRequestInvalidMethodException("m", cause).getCause());
    }

    @Test
    void invalidRequestTarget() {
        assertEquals("m", new HttpRequestInvalidRequestTargetException("m").getMessage());
        assertSame(cause, new HttpRequestInvalidRequestTargetException("m", cause).getCause());
    }

    @Test
    void abstractRuntimeBase() {
        assertEquals("m", new HttpRequestRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpRequestRuntimeException("m", cause) {}.getCause());
    }
}