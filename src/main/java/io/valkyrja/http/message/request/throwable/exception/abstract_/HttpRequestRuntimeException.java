/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.throwable.exception.abstract_;

import io.valkyrja.http.message.request.throwable.contract.HttpRequestThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public abstract class HttpRequestRuntimeException extends HttpMessageRuntimeException
        implements HttpRequestThrowable {

    protected HttpRequestRuntimeException(String message) {
        super(message);
    }

    protected HttpRequestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
