/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.client.throwable.exception.abstract_;

import io.valkyrja.http.client.throwable.contract.HttpClientThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpClientInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpClientThrowable {

    protected HttpClientInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpClientInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
