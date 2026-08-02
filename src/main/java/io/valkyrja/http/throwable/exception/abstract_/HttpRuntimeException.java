/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.throwable.exception.abstract_;

import io.valkyrja.http.throwable.contract.HttpThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class HttpRuntimeException extends RuntimeException implements HttpThrowable {

    protected HttpRuntimeException(String message) {
        super(message);
    }

    protected HttpRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
