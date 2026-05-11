/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.throwable.exception.abstract_;

import io.valkyrja.log.throwable.contract.LogThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class LogInvalidArgumentException extends InvalidArgumentException implements LogThrowable {

    public LogInvalidArgumentException(String message) {
        super(message);
    }

    public LogInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
