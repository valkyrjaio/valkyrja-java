/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.throwable.exception;

import io.valkyrja.http.message.uri.throwable.exception.abstract_.HttpUriRuntimeException;

public class NoPortExceptionHttpUri extends HttpUriRuntimeException {

    public NoPortExceptionHttpUri(String message) {
        super(message);
    }

    public NoPortExceptionHttpUri(String message, Throwable cause) {
        super(message, cause);
    }
}
