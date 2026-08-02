/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response.throwable.exception.abstract_;

import io.valkyrja.http.message.response.throwable.contract.HttpResponseThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public abstract class HttpResponseRuntimeException extends HttpMessageRuntimeException
        implements HttpResponseThrowable {

    protected HttpResponseRuntimeException(String message) {
        super(message);
    }

    protected HttpResponseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
