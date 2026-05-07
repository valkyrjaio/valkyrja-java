/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.throwable.exception;

import io.valkyrja.throwable.contract.ThrowableContract;
import io.valkyrja.throwable.handler.abstract_.ThrowableHandler;

/** Base checked exception for the Valkyrja framework. */
public class Exception extends java.lang.Exception implements ThrowableContract {

    public Exception(String message) {
        super(message);
    }

    public Exception(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    /**
     * Create and throw this exception type.
     *
     * @param message the detail message
     * @throws Exception always
     */
    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    @Override
    public String getTraceCode() {
        return ThrowableHandler.getTraceCode(this);
    }
}
