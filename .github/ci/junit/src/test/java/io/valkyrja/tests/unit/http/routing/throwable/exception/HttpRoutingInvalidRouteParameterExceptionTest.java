/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteParameterException;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingInvalidRouteParameterException}. */
final class HttpRoutingInvalidRouteParameterExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new HttpRoutingInvalidRouteParameterException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new HttpRoutingInvalidRouteParameterException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
