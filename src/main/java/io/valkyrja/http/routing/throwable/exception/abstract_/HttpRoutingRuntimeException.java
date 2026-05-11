/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.throwable.exception.abstract_;

import io.valkyrja.http.routing.throwable.contract.HttpRoutingThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpRoutingRuntimeException extends HttpRuntimeException
        implements HttpRoutingThrowable {

    protected HttpRoutingRuntimeException(String message) {
        super(message);
    }

    protected HttpRoutingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
