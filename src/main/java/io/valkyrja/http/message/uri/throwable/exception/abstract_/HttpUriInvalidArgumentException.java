/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.throwable.exception.abstract_;

import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;
import io.valkyrja.http.message.uri.throwable.contract.HttpUriThrowable;

public abstract class HttpUriInvalidArgumentException extends HttpMessageInvalidArgumentException
        implements HttpUriThrowable {

    protected HttpUriInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpUriInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
