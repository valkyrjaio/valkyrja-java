/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception.abstract_;

import io.valkyrja.container.throwable.contract.ContainerThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class ContainerInvalidArgumentException extends InvalidArgumentException
        implements ContainerThrowable {

    protected ContainerInvalidArgumentException(String message) {
        super(message);
    }

    protected ContainerInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
