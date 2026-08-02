/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.throwable.exception;

import io.valkyrja.http.message.header.throwable.exception.abstract_.HttpHeaderInvalidArgumentException;

public class HttpHeaderInvalidHeaderParamException extends HttpHeaderInvalidArgumentException {

    public HttpHeaderInvalidHeaderParamException(String message) {
        super(message);
    }

    public HttpHeaderInvalidHeaderParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
