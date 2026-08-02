/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.throwable.exception;

import io.valkyrja.http.routing.throwable.exception.abstract_.HttpRoutingInvalidArgumentException;

public class HttpRoutingInvalidMethodTypeException extends HttpRoutingInvalidArgumentException {

    public HttpRoutingInvalidMethodTypeException(String message) {
        super(message);
    }

    public HttpRoutingInvalidMethodTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
