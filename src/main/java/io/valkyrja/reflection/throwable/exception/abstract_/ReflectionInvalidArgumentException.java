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
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class ReflectionInvalidArgumentException extends InvalidArgumentException
        implements ReflectionThrowable {

    protected ReflectionInvalidArgumentException(String message) {
        super(message);
    }

    protected ReflectionInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}