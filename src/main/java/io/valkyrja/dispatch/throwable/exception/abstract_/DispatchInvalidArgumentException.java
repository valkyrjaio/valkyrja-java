/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.throwable.exception.abstract_;

import io.valkyrja.dispatch.throwable.contract.DispatchThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class DispatchInvalidArgumentException extends InvalidArgumentException
        implements DispatchThrowable {

    protected DispatchInvalidArgumentException(String message) {
        super(message);
    }

    protected DispatchInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
