/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.throwable.exception.abstract_;

import io.valkyrja.http.server.throwable.contract.HttpServerThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpServerInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpServerThrowable {

    protected HttpServerInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpServerInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
