/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.throwable.exception;

import io.valkyrja.http.routing.throwable.exception.abstract_.HttpRoutingInvalidArgumentException;

public class HttpRoutingInvalidRouteParameterException extends HttpRoutingInvalidArgumentException {

    public HttpRoutingInvalidRouteParameterException(String message) {
        super(message);
    }

    public HttpRoutingInvalidRouteParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}