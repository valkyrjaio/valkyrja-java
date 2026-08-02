/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.throwable.exception;

import io.valkyrja.throwable.contract.ThrowableContract;
import io.valkyrja.throwable.handler.abstract_.ThrowableHandler;

/** Base checked exception for the Valkyrja framework. */
public class Exception extends java.lang.Exception implements ThrowableContract {

    public Exception(String message) {
        super(message);
    }

    public Exception(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    /**
     * Create and throw this exception type.
     *
     * @param message the detail message
     * @throws Exception always
     */
    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    @Override
    public String getTraceCode() {
        return ThrowableHandler.getTraceCode(this);
    }
}
