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

/** Base unchecked (runtime) exception for the Valkyrja framework. */
public class RuntimeException extends java.lang.RuntimeException implements ThrowableContract {

    public RuntimeException(String message) {
        super(message);
    }

    public RuntimeException(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    /**
     * Create and throw this exception type.
     *
     * @param message the detail message
     */
    public static void throwException(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public String getTraceCode() {
        return ThrowableHandler.getTraceCode(this);
    }
}
