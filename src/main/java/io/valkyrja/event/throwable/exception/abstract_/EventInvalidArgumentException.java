/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.throwable.exception.abstract_;

import io.valkyrja.event.throwable.contract.EventThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class EventInvalidArgumentException extends InvalidArgumentException
        implements EventThrowable {

    protected EventInvalidArgumentException(String message) {
        super(message);
    }

    protected EventInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
