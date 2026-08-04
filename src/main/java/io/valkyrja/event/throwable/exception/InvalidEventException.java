/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.throwable.exception;

import io.valkyrja.event.throwable.exception.abstract_.EventInvalidArgumentException;

/**
 * Reports a binding key that the container resolves to a value of another type.
 *
 * <p>The dispatcher builds an event through the container. The container resolves a binding key to
 * any value at all, so the dispatcher tests the value against the key.
 */
public class InvalidEventException extends EventInvalidArgumentException {

    public InvalidEventException(String id) {
        super("Service with `" + id + "` is not an event");
    }
}
