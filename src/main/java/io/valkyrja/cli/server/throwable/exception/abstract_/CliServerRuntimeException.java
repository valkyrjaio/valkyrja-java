/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.throwable.exception.abstract_;

import io.valkyrja.cli.server.throwable.contract.CliServerThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliRuntimeException;

public abstract class CliServerRuntimeException extends CliRuntimeException
        implements CliServerThrowable {

    protected CliServerRuntimeException(String message) {
        super(message);
    }

    protected CliServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
