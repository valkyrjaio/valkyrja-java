/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.throwable.exception;

import io.valkyrja.log.throwable.exception.abstract_.LogInvalidArgumentException;

public class LogInvalidLogLevelException extends LogInvalidArgumentException {

    public LogInvalidLogLevelException(String message) {
        super(message);
    }

    public LogInvalidLogLevelException(String message, Throwable cause) {
        super(message, cause);
    }
}