/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.throwable.exception.abstract_;

import io.valkyrja.http.throwable.contract.HttpThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class HttpInvalidArgumentException extends InvalidArgumentException
        implements HttpThrowable {

    protected HttpInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
