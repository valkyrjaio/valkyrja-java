/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.throwable.exception;

import io.valkyrja.http.message.request.throwable.exception.abstract_.HttpRequestInvalidArgumentException;

public class HttpRequestInvalidMethodException extends HttpRequestInvalidArgumentException {

    public HttpRequestInvalidMethodException(String message) {
        super(message);
    }

    public HttpRequestInvalidMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
