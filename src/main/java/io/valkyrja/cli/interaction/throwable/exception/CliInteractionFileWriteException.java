/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.throwable.exception;

import io.valkyrja.cli.interaction.throwable.exception.abstract_.CliInteractionRuntimeException;

public class CliInteractionFileWriteException extends CliInteractionRuntimeException {

    public CliInteractionFileWriteException(String message) {
        super(message);
    }

    public CliInteractionFileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
