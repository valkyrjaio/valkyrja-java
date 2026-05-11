/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream.throwable.exception.abstract_;

import io.valkyrja.http.message.stream.throwable.contract.HttpStreamThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public abstract class HttpStreamRuntimeException extends HttpMessageRuntimeException
        implements HttpStreamThrowable {

    protected HttpStreamRuntimeException(String message) {
        super(message);
    }

    protected HttpStreamRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
