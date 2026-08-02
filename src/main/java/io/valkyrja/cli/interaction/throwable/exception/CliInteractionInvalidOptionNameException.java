/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.throwable.exception;

import io.valkyrja.cli.interaction.throwable.exception.abstract_.CliInteractionInvalidArgumentException;

public class CliInteractionInvalidOptionNameException
        extends CliInteractionInvalidArgumentException {

    public CliInteractionInvalidOptionNameException(String message) {
        super(message);
    }

    public CliInteractionInvalidOptionNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
