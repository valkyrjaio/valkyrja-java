/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.throwable.exception;

import io.valkyrja.http.message.uri.throwable.exception.abstract_.HttpUriInvalidArgumentException;

public class HttpUriInvalidQueryException extends HttpUriInvalidArgumentException {

    public HttpUriInvalidQueryException(String message) {
        super(message);
    }

    public HttpUriInvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
