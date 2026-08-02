/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.throwable.exception;

import io.valkyrja.cli.routing.throwable.exception.abstract_.CliRoutingRuntimeException;

public class CliRoutingNoHelpTextException extends CliRoutingRuntimeException {

    public CliRoutingNoHelpTextException(String message) {
        super(message);
    }

    public CliRoutingNoHelpTextException(String message, Throwable cause) {
        super(message, cause);
    }
}
