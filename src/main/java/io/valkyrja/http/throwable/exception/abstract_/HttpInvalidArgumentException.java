/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.throwable.exception.abstract_;

import io.valkyrja.http.throwable.contract.HttpThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class HttpInvalidArgumentException extends InvalidArgumentException
        implements HttpThrowable {

    protected HttpInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
