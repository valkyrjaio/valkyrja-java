/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.throwable.exception.abstract_;

import io.valkyrja.cli.routing.throwable.contract.CliRoutingThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliInvalidArgumentException;

public abstract class CliRoutingInvalidArgumentException extends CliInvalidArgumentException
        implements CliRoutingThrowable {

    protected CliRoutingInvalidArgumentException(String message) {
        super(message);
    }

    protected CliRoutingInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
