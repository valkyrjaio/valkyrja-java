/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.throwable.exception.abstract_;

import io.valkyrja.application.throwable.contract.ApplicationThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class ApplicationRuntimeException extends RuntimeException
        implements ApplicationThrowable {

    protected ApplicationRuntimeException(String message) {
        super(message);
    }

    protected ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
