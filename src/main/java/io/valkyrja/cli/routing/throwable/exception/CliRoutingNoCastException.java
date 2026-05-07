/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.throwable.exception;

import io.valkyrja.cli.routing.throwable.exception.abstract_.CliRoutingRuntimeException;

public class CliRoutingNoCastException extends CliRoutingRuntimeException {

    public CliRoutingNoCastException(String message) {
        super(message);
    }

    public CliRoutingNoCastException(String message, Throwable cause) {
        super(message, cause);
    }
}
