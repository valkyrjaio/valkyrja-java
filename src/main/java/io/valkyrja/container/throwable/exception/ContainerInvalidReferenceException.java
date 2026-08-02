/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;

public class ContainerInvalidReferenceException extends ContainerInvalidArgumentException {

    public ContainerInvalidReferenceException(String id) {
        super("Service with `" + id + "` not found");
    }

    public ContainerInvalidReferenceException(String id, Throwable cause) {
        super("Service with `" + id + "` not found", cause);
    }
}
