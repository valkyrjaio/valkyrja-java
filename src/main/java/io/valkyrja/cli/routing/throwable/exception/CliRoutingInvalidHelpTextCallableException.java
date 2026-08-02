/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.throwable.exception;

import io.valkyrja.cli.routing.throwable.exception.abstract_.CliRoutingInvalidArgumentException;

public class CliRoutingInvalidHelpTextCallableException extends CliRoutingInvalidArgumentException {

    public CliRoutingInvalidHelpTextCallableException(String message) {
        super(message);
    }

    public CliRoutingInvalidHelpTextCallableException(String message, Throwable cause) {
        super(message, cause);
    }
}
