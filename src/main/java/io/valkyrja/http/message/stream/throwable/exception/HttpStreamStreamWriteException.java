/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.throwable.exception;

import io.valkyrja.http.message.stream.throwable.exception.abstract_.HttpStreamRuntimeException;

public class HttpStreamStreamWriteException extends HttpStreamRuntimeException {

    public HttpStreamStreamWriteException(String message) {
        super(message);
    }

    public HttpStreamStreamWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
