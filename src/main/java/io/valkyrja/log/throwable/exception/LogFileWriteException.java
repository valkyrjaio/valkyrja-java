/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.throwable.exception;

import io.valkyrja.log.throwable.exception.abstract_.LogRuntimeException;

public class LogFileWriteException extends LogRuntimeException {

    public LogFileWriteException(String message) {
        super(message);
    }

    public LogFileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
