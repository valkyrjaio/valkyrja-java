/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.client.throwable.exception.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.client.throwable.exception.abstract_.HttpClientInvalidArgumentException;
import io.valkyrja.http.client.throwable.exception.abstract_.HttpClientRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the abstract http client throwable bases (no concrete subclasses exist). */
final class HttpClientExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void invalidArgumentBase() {
        assertEquals("m", new HttpClientInvalidArgumentException("m") {}.getMessage());
        assertSame(cause, new HttpClientInvalidArgumentException("m", cause) {}.getCause());
    }

    @Test
    void runtimeBase() {
        assertEquals("m", new HttpClientRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpClientRuntimeException("m", cause) {}.getCause());
    }
}
