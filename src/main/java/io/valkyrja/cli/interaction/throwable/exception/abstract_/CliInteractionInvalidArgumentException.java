/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
