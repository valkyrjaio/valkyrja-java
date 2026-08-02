/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.throwable.exception.abstract_;

import io.valkyrja.cli.interaction.throwable.contract.CliInteractionThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliRuntimeException;

public abstract class CliInteractionRuntimeException extends CliRuntimeException
        implements CliInteractionThrowable {

    protected CliInteractionRuntimeException(String message) {
        super(message);
    }

    protected CliInteractionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
