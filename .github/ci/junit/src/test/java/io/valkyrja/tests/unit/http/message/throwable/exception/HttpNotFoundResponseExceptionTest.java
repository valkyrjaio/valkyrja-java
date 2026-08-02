/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
