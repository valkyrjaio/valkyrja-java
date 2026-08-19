/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.throwable.exception;

import io.valkyrja.cli.interaction.throwable.exception.abstract_.CliInteractionRuntimeException;

public class CliInteractionUnwritableFileException extends CliInteractionRuntimeException {

    public CliInteractionUnwritableFileException(String message) {
        super(message);
    }

    public CliInteractionUnwritableFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
