/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.throwable.exception.HttpResponseException;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpResponseException}. */
final class HttpResponseExceptionTest {

    @Test
    void defaultsToInternalServerErrorWhenNoStatusOrResponse() {
        var exception = new HttpResponseException(null, null, null, null);

        assertEquals(StatusCode.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("", exception.getMessage());
        assertNotNull(exception.getHeaders());
        assertNull(exception.getResponse());
    }

    @Test
    void derivesStatusFromResponse() {
        var exception = new HttpResponseException(null, "msg", new HeaderCollection(), new EmptyResponse());

        assertEquals(StatusCode.NO_CONTENT, exception.getStatusCode());
        assertEquals("msg", exception.getMessage());
        assertNotNull(exception.getResponse());
    }

    @Test
    void explicitStatusOverridesResponseStatus() {
        var exception =
                new HttpResponseException(
                        StatusCode.BAD_REQUEST, "bad", new HeaderCollection(), new EmptyResponse());

        assertEquals(StatusCode.BAD_REQUEST, exception.getStatusCode());
        assertEquals(StatusCode.BAD_REQUEST, exception.getResponse().getStatusCode());
    }
}
