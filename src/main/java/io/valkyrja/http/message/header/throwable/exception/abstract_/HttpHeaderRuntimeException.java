/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.throwable.exception.abstract_;

import io.valkyrja.http.message.header.throwable.contract.HttpHeaderThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public abstract class HttpHeaderRuntimeException extends HttpMessageRuntimeException
        implements HttpHeaderThrowable {

    protected HttpHeaderRuntimeException(String message) {
        super(message);
    }

    protected HttpHeaderRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
