/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.throwable.exception.abstract_;

import io.valkyrja.cli.server.throwable.contract.CliServerThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliRuntimeException;

public abstract class CliServerRuntimeException extends CliRuntimeException
        implements CliServerThrowable {

    protected CliServerRuntimeException(String message) {
        super(message);
    }

    protected CliServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
