/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.throwable.exception;

import io.valkyrja.http.message.uri.throwable.exception.abstract_.HttpUriInvalidArgumentException;

public class HttpUriInvalidQueryException extends HttpUriInvalidArgumentException {

    public HttpUriInvalidQueryException(String message) {
        super(message);
    }

    public HttpUriInvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}