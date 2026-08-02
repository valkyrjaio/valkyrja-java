/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.throwable.exception;

import io.valkyrja.http.message.uri.throwable.exception.abstract_.HttpUriInvalidArgumentException;

public class HttpUriInvalidPortException extends HttpUriInvalidArgumentException {

    public HttpUriInvalidPortException(String message) {
        super(message);
    }

    public HttpUriInvalidPortException(String message, Throwable cause) {
        super(message, cause);
    }
}
