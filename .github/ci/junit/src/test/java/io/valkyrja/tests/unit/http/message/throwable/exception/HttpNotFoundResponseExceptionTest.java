/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.throwable.exception.HttpNotFoundResponseException;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpNotFoundResponseException}. */
final class HttpNotFoundResponseExceptionTest {

    @Test
    void defaultsToNotFoundStatus() {
        var exception = new HttpNotFoundResponseException(null, "missing", new HeaderCollection());

        assertEquals(StatusCode.NOT_FOUND, exception.getStatusCode());
        assertEquals("missing", exception.getMessage());
    }

    @Test
    void respectsExplicitStatus() {
        var exception =
                new HttpNotFoundResponseException(StatusCode.GONE, "gone", new HeaderCollection());

        assertEquals(StatusCode.GONE, exception.getStatusCode());
    }
}
