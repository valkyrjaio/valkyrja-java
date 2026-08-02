/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.throwable.exception.abstract_;

import io.valkyrja.log.throwable.contract.LogThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class LogInvalidArgumentException extends InvalidArgumentException
        implements LogThrowable {

    public LogInvalidArgumentException(String message) {
        super(message);
    }

    public LogInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
