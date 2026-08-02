/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.throwable.exception.abstract_;

import io.valkyrja.http.message.stream.throwable.contract.HttpStreamThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public abstract class HttpStreamRuntimeException extends HttpMessageRuntimeException
        implements HttpStreamThrowable {

    protected HttpStreamRuntimeException(String message) {
        super(message);
    }

    protected HttpStreamRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
