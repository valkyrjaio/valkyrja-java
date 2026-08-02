/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.throwable.exception;

import io.valkyrja.dispatch.throwable.exception.abstract_.DispatchInvalidArgumentException;

public class DispatchInvalidDispatchCapabilityException extends DispatchInvalidArgumentException {

    public DispatchInvalidDispatchCapabilityException(String message) {
        super(message);
    }

    public DispatchInvalidDispatchCapabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
