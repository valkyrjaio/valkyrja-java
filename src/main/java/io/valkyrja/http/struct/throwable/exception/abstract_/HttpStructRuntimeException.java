/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.throwable.exception.abstract_;

import io.valkyrja.http.struct.throwable.contract.HttpStructThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpStructRuntimeException extends HttpRuntimeException
        implements HttpStructThrowable {

    protected HttpStructRuntimeException(String message) {
        super(message);
    }

    protected HttpStructRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
