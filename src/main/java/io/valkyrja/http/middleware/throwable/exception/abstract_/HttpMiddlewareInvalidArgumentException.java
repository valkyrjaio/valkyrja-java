/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.throwable.exception.abstract_;

import io.valkyrja.http.middleware.throwable.contract.HttpMiddlewareThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpMiddlewareInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpMiddlewareThrowable {

    protected HttpMiddlewareInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpMiddlewareInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}