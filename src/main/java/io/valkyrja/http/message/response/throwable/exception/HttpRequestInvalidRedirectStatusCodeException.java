/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response.throwable.exception;

import io.valkyrja.http.message.response.throwable.exception.abstract_.HttpResponseInvalidArgumentException;

public class HttpRequestInvalidRedirectStatusCodeException
        extends HttpResponseInvalidArgumentException {

    public HttpRequestInvalidRedirectStatusCodeException(String message) {
        super(message);
    }

    public HttpRequestInvalidRedirectStatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
