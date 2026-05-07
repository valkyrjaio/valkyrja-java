/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.throwable.exception;

import io.valkyrja.cli.interaction.throwable.exception.abstract_.CliInteractionRuntimeException;

public class CliInteractionNoValidationCallableException extends CliInteractionRuntimeException {

    public CliInteractionNoValidationCallableException(String message) {
        super(message);
    }

    public CliInteractionNoValidationCallableException(String message, Throwable cause) {
        super(message, cause);
    }
}
