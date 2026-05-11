/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.throwable.exception.abstract_;

import io.valkyrja.container.throwable.contract.ContainerThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class ContainerInvalidArgumentException extends InvalidArgumentException
        implements ContainerThrowable {

    protected ContainerInvalidArgumentException(String message) {
        super(message);
    }

    protected ContainerInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
