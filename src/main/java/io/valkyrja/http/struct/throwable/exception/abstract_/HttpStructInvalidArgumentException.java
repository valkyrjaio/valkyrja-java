/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.throwable.exception.abstract_;

import io.valkyrja.http.struct.throwable.contract.HttpStructThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpStructInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpStructThrowable {

    protected HttpStructInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpStructInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
