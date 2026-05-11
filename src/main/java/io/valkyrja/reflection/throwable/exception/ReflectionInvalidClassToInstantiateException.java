/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.reflection.throwable.exception;

import io.valkyrja.reflection.throwable.exception.abstract_.ReflectionRuntimeException;

public class ReflectionInvalidClassToInstantiateException extends ReflectionRuntimeException {

    public ReflectionInvalidClassToInstantiateException(String message) {
        super(message);
    }

    public ReflectionInvalidClassToInstantiateException(String message, Throwable cause) {
        super(message, cause);
    }
}
