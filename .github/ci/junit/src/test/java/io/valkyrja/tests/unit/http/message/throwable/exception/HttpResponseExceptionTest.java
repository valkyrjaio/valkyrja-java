/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.throwable.exception;

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
        var exception =
                new HttpResponseException(null, "msg", new HeaderCollection(), new EmptyResponse());

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
