/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.throwable.exception.abstract_;

import io.valkyrja.http.middleware.throwable.contract.HttpMiddlewareThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpMiddlewareInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpMiddlewareThrowable {

    protected HttpMiddlewareInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpMiddlewareInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
