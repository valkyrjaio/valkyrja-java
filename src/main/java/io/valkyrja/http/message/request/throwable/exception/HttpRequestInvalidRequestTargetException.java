/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.throwable.exception;

import io.valkyrja.http.message.request.throwable.exception.abstract_.HttpRequestInvalidArgumentException;

public class HttpRequestInvalidRequestTargetException extends HttpRequestInvalidArgumentException {

    public HttpRequestInvalidRequestTargetException(String message) {
        super(message);
    }

    public HttpRequestInvalidRequestTargetException(String message, Throwable cause) {
        super(message, cause);
    }
}
