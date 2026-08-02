/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.throwable.exception.abstract_;

import io.valkyrja.http.server.throwable.contract.HttpServerThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpServerRuntimeException extends HttpRuntimeException
        implements HttpServerThrowable {

    protected HttpServerRuntimeException(String message) {
        super(message);
    }

    protected HttpServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
