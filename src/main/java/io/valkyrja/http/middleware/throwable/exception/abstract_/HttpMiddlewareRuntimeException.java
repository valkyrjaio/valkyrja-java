/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.throwable.exception.abstract_;

import io.valkyrja.http.middleware.throwable.contract.HttpMiddlewareThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpMiddlewareRuntimeException extends HttpRuntimeException
        implements HttpMiddlewareThrowable {

    protected HttpMiddlewareRuntimeException(String message) {
        super(message);
    }

    protected HttpMiddlewareRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
