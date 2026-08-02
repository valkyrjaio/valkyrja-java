/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.throwable.exception.abstract_;

import io.valkyrja.http.message.request.throwable.contract.HttpRequestThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;

public abstract class HttpRequestInvalidArgumentException
        extends HttpMessageInvalidArgumentException implements HttpRequestThrowable {

    protected HttpRequestInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpRequestInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
