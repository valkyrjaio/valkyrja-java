/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.throwable.exception.abstract_;

import io.valkyrja.cli.middleware.throwable.contract.CliMiddlewareThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliRuntimeException;

public abstract class CliMiddlewareRuntimeException extends CliRuntimeException
        implements CliMiddlewareThrowable {

    protected CliMiddlewareRuntimeException(String message) {
        super(message);
    }

    protected CliMiddlewareRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
