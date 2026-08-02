/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.throwable.exception.abstract_;

import io.valkyrja.http.server.throwable.contract.HttpServerThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpServerInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpServerThrowable {

    protected HttpServerInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpServerInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
