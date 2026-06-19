/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.struct.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.struct.throwable.exception.HttpStructJsonServerRequestExpectedException;
import io.valkyrja.http.struct.throwable.exception.abstract_.HttpStructRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the http struct throwables and their abstract argument/runtime bases. */
final class HttpStructExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void jsonServerRequestExpected() {
        assertEquals("m", new HttpStructJsonServerRequestExpectedException("m").getMessage());
        assertSame(cause, new HttpStructJsonServerRequestExpectedException("m", cause).getCause());
    }

    @Test
    void runtimeBase() {
        assertEquals("m", new HttpStructRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpStructRuntimeException("m", cause) {}.getCause());
    }
}
