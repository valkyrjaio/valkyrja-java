/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.throwable.exception.abstract_;

import io.valkyrja.cli.throwable.contract.CliThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class CliRuntimeException extends RuntimeException implements CliThrowable {

    protected CliRuntimeException(String message) {
        super(message);
    }

    protected CliRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
