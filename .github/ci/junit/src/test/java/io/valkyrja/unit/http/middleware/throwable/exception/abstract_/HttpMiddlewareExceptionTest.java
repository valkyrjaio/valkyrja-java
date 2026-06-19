/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.middleware.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.middleware.throwable.exception.abstract_.HttpMiddlewareInvalidArgumentException;
import io.valkyrja.http.middleware.throwable.exception.abstract_.HttpMiddlewareRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the abstract http middleware throwable bases (no concrete subclasses exist). */
final class HttpMiddlewareExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void invalidArgumentBase() {
        assertEquals("m", new HttpMiddlewareInvalidArgumentException("m") {}.getMessage());
        assertSame(cause, new HttpMiddlewareInvalidArgumentException("m", cause) {}.getCause());
    }

    @Test
    void runtimeBase() {
        assertEquals("m", new HttpMiddlewareRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpMiddlewareRuntimeException("m", cause) {}.getCause());
    }
}
