/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.throwable.exception.abstract_;

import io.valkyrja.http.message.stream.throwable.contract.HttpStreamThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;

public abstract class HttpStreamInvalidArgumentException extends HttpMessageInvalidArgumentException
        implements HttpStreamThrowable {

    protected HttpStreamInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpStreamInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
