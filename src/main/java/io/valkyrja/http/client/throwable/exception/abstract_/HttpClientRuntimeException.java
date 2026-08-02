/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.client.throwable.exception.abstract_;

import io.valkyrja.http.client.throwable.contract.HttpClientThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpClientRuntimeException extends HttpRuntimeException
        implements HttpClientThrowable {

    protected HttpClientRuntimeException(String message) {
        super(message);
    }

    protected HttpClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
