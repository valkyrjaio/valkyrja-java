/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.throwable.exception.abstract_;

import io.valkyrja.cli.routing.throwable.contract.CliRoutingThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliInvalidArgumentException;

public abstract class CliRoutingInvalidArgumentException extends CliInvalidArgumentException
        implements CliRoutingThrowable {

    protected CliRoutingInvalidArgumentException(String message) {
        super(message);
    }

    protected CliRoutingInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
