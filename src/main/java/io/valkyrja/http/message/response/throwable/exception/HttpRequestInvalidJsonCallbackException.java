/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response.throwable.exception;

import io.valkyrja.http.message.response.throwable.exception.abstract_.HttpResponseInvalidArgumentException;

public class HttpRequestInvalidJsonCallbackException extends HttpResponseInvalidArgumentException {

    public HttpRequestInvalidJsonCallbackException(String message) {
        super(message);
    }

    public HttpRequestInvalidJsonCallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
