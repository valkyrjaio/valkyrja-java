/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.server.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.server.throwable.exception.abstract_.HttpServerInvalidArgumentException;
import io.valkyrja.http.server.throwable.exception.abstract_.HttpServerRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the abstract http server throwable bases (no concrete subclasses exist). */
final class HttpServerExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void invalidArgumentBase() {
        assertEquals("m", new HttpServerInvalidArgumentException("m") {}.getMessage());
        assertSame(cause, new HttpServerInvalidArgumentException("m", cause) {}.getCause());
    }

    @Test
    void runtimeBase() {
        assertEquals("m", new HttpServerRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpServerRuntimeException("m", cause) {}.getCause());
    }
}
