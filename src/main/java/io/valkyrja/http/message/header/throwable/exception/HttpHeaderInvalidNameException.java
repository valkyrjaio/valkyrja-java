/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.throwable.exception;

import io.valkyrja.http.message.header.throwable.exception.abstract_.HttpHeaderInvalidArgumentException;

public class HttpHeaderInvalidNameException extends HttpHeaderInvalidArgumentException {

    public HttpHeaderInvalidNameException(String message) {
        super(message);
    }

    public HttpHeaderInvalidNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
