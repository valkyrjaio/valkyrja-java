/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.reflection.throwable.exception.abstract_;

import io.valkyrja.reflection.throwable.contract.ReflectionThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class ReflectionRuntimeException extends RuntimeException
        implements ReflectionThrowable {

    protected ReflectionRuntimeException(String message) {
        super(message);
    }

    protected ReflectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
