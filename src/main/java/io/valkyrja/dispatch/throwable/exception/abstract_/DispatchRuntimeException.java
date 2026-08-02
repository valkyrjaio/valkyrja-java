/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.throwable.exception.abstract_;

import io.valkyrja.dispatch.throwable.contract.DispatchThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class DispatchRuntimeException extends RuntimeException
        implements DispatchThrowable {

    protected DispatchRuntimeException(String message) {
        super(message);
    }

    protected DispatchRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
