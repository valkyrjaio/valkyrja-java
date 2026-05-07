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

public class CliInteractionNoFormatterException extends CliInteractionRuntimeException {

    public CliInteractionNoFormatterException(String message) {
        super(message);
    }

    public CliInteractionNoFormatterException(String message, Throwable cause) {
        super(message, cause);
    }
}
