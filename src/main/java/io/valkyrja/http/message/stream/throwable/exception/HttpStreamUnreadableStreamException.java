/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.throwable.exception;

import io.valkyrja.http.message.stream.throwable.exception.abstract_.HttpStreamRuntimeException;

public class HttpStreamUnreadableStreamException extends HttpStreamRuntimeException {

    public HttpStreamUnreadableStreamException(String message) {
        super(message);
    }

    public HttpStreamUnreadableStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
