/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.throwable.exception.abstract_;

import io.valkyrja.cli.throwable.contract.CliThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class CliInvalidArgumentException extends InvalidArgumentException
        implements CliThrowable {

    protected CliInvalidArgumentException(String message) {
        super(message);
    }

    protected CliInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
