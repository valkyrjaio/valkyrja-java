/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.reflection.throwable.exception;

import io.valkyrja.reflection.throwable.exception.abstract_.ReflectionRuntimeException;

public class ReflectionInvalidClassToInstantiateException extends ReflectionRuntimeException {

    public ReflectionInvalidClassToInstantiateException(String message) {
        super(message);
    }

    public ReflectionInvalidClassToInstantiateException(String message, Throwable cause) {
        super(message, cause);
    }
}
