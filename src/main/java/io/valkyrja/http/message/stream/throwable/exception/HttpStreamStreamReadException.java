/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream.throwable.exception;

import io.valkyrja.http.message.stream.throwable.exception.abstract_.HttpStreamRuntimeException;

public class HttpStreamStreamReadException extends HttpStreamRuntimeException {

    public HttpStreamStreamReadException(String message) {
        super(message);
    }

    public HttpStreamStreamReadException(String message, Throwable cause) {
        super(message, cause);
    }
}