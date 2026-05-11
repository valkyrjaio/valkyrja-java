/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.throwable.exception.abstract_;

import io.valkyrja.http.message.throwable.contract.HttpMessageThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpMessageRuntimeException extends HttpRuntimeException
        implements HttpMessageThrowable {

    protected HttpMessageRuntimeException(String message) {
        super(message);
    }

    protected HttpMessageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
