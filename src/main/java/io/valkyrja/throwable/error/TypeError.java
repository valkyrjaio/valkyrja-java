/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.throwable.error;

import io.valkyrja.throwable.contract.ThrowableContract;
import io.valkyrja.throwable.handler.abstract_.ThrowableHandler;

/**
 * Type error for the Valkyrja framework.
 *
 * <p>Corresponds to PHP's {@code TypeError}. Extends {@link java.lang.RuntimeException} rather than
 * Java's {@link java.lang.Error} since application-level type errors are not JVM errors.
 */
public class TypeError extends java.lang.RuntimeException implements ThrowableContract {

    public TypeError(String message) {
        super(message);
    }

    public TypeError(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTraceCode() {
        return ThrowableHandler.getTraceCode(this);
    }
}
