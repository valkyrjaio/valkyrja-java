/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.reflection.throwable.exception.abstract_;

import io.valkyrja.reflection.throwable.contract.ReflectionThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class ReflectionInvalidArgumentException extends InvalidArgumentException
        implements ReflectionThrowable {

    protected ReflectionInvalidArgumentException(String message) {
        super(message);
    }

    protected ReflectionInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
