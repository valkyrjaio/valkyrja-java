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

public class CliRoutingArgumentValuesValidationException extends CliRoutingRuntimeException {

    public CliRoutingArgumentValuesValidationException(String message) {
        super(message);
    }

    public CliRoutingArgumentValuesValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
