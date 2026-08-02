/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.throwable.exception;

import io.valkyrja.log.throwable.exception.abstract_.LogRuntimeException;

public class LogFileWriteException extends LogRuntimeException {

    public LogFileWriteException(String message) {
        super(message);
    }

    public LogFileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
