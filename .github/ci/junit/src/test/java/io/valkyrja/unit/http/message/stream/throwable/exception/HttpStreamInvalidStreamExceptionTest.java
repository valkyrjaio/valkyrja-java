/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.stream.throwable.exception.HttpStreamInvalidStreamException;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpStreamInvalidStreamException}. */
final class HttpStreamInvalidStreamExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new HttpStreamInvalidStreamException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new HttpStreamInvalidStreamException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
