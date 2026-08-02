/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.throwable.exception;

import io.valkyrja.cli.routing.throwable.exception.abstract_.CliRoutingRuntimeException;

public class CliRoutingArgumentValuesValidationException extends CliRoutingRuntimeException {

    public CliRoutingArgumentValuesValidationException(String message) {
        super(message);
    }

    public CliRoutingArgumentValuesValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
