/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response.throwable.exception;

import io.valkyrja.http.message.response.throwable.exception.abstract_.HttpResponseInvalidArgumentException;

public class HttpRequestInvalidJsonCallbackException extends HttpResponseInvalidArgumentException {

    public HttpRequestInvalidJsonCallbackException(String message) {
        super(message);
    }

    public HttpRequestInvalidJsonCallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}