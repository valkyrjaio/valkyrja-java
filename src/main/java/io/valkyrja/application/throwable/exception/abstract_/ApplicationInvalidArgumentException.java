/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.throwable.exception.abstract_;

import io.valkyrja.application.throwable.contract.ApplicationThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class ApplicationInvalidArgumentException extends InvalidArgumentException
        implements ApplicationThrowable {

    protected ApplicationInvalidArgumentException(String message) {
        super(message);
    }

    protected ApplicationInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
