/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.throwable.exception.abstract_;

import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;
import io.valkyrja.http.message.uri.throwable.contract.HttpUriThrowable;

public abstract class HttpUriRuntimeException extends HttpMessageRuntimeException
        implements HttpUriThrowable {

    protected HttpUriRuntimeException(String message) {
        super(message);
    }

    protected HttpUriRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
