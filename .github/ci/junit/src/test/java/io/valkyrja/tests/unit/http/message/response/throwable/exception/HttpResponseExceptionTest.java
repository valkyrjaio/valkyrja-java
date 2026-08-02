/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.response.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidJsonCallbackException;
import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidRedirectStatusCodeException;
import io.valkyrja.http.message.response.throwable.exception.abstract_.HttpResponseRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the http response throwables, including the abstract runtime base. */
final class HttpResponseExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void invalidJsonCallback() {
        assertEquals("m", new HttpRequestInvalidJsonCallbackException("m").getMessage());
        assertSame(cause, new HttpRequestInvalidJsonCallbackException("m", cause).getCause());
    }

    @Test
    void invalidRedirectStatusCode() {
        assertEquals("m", new HttpRequestInvalidRedirectStatusCodeException("m").getMessage());
        assertSame(cause, new HttpRequestInvalidRedirectStatusCodeException("m", cause).getCause());
    }

    @Test
    void abstractRuntimeBase() {
        assertEquals("m", new HttpResponseRuntimeException("m") {}.getMessage());
        assertSame(cause, new HttpResponseRuntimeException("m", cause) {}.getCause());
    }
}
