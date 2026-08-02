/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerRuntimeException;

public class ContainerInvalidPublishCallbackException extends ContainerRuntimeException {

    public ContainerInvalidPublishCallbackException(String message) {
        super(message);
    }

    public ContainerInvalidPublishCallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
