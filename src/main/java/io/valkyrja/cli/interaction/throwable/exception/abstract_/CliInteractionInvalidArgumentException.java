/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.throwable.exception.abstract_;

import io.valkyrja.cli.interaction.throwable.contract.CliInteractionThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliInvalidArgumentException;

public abstract class CliInteractionInvalidArgumentException extends CliInvalidArgumentException
        implements CliInteractionThrowable {

    protected CliInteractionInvalidArgumentException(String message) {
        super(message);
    }

    protected CliInteractionInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
