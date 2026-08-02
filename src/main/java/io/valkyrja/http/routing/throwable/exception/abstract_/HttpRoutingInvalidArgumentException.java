/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.throwable.exception.abstract_;

import io.valkyrja.http.routing.throwable.contract.HttpRoutingThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpRoutingInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpRoutingThrowable {

    protected HttpRoutingInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpRoutingInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
