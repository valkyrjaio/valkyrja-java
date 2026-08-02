/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.throwable.exception;

public class HttpHeaderUnsupportedOffsetSetException extends HttpHeaderUnsupportedMethodException {

    public HttpHeaderUnsupportedOffsetSetException(String message) {
        super(message);
    }

    public HttpHeaderUnsupportedOffsetSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
