/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.throwable.exception;

import io.valkyrja.dispatch.throwable.exception.abstract_.DispatchRuntimeException;

public class DispatchNoClassException extends DispatchRuntimeException {

    public DispatchNoClassException(String message) {
        super(message);
    }

    public DispatchNoClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
