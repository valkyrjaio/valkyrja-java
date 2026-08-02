/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.throwable.exception.abstract_;

import io.valkyrja.event.throwable.contract.EventThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class EventInvalidArgumentException extends InvalidArgumentException
        implements EventThrowable {

    protected EventInvalidArgumentException(String message) {
        super(message);
    }

    protected EventInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
