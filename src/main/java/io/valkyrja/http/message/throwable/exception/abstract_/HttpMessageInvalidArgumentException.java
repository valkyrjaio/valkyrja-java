/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.throwable.exception.abstract_;

import io.valkyrja.http.message.throwable.contract.HttpMessageThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpMessageInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpMessageThrowable {

    protected HttpMessageInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpMessageInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
