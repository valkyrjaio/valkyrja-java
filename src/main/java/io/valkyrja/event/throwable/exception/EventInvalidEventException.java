/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.throwable.exception;

import io.valkyrja.event.throwable.exception.abstract_.EventInvalidArgumentException;

public class EventInvalidEventException extends EventInvalidArgumentException {

    public EventInvalidEventException(String id) {
        super("Service with `" + id + "` is not an event");
    }
}
