/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.reflection.throwable.exception.abstract_;

import io.valkyrja.reflection.throwable.contract.ReflectionThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class ReflectionRuntimeException extends RuntimeException
        implements ReflectionThrowable {

    protected ReflectionRuntimeException(String message) {
        super(message);
    }

    protected ReflectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}