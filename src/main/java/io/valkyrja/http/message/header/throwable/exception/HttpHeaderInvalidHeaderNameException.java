/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.throwable.exception;

import io.valkyrja.http.message.header.throwable.exception.abstract_.HttpHeaderInvalidArgumentException;

public class HttpHeaderInvalidHeaderNameException extends HttpHeaderInvalidArgumentException {

    public HttpHeaderInvalidHeaderNameException(String message) {
        super(message);
    }

    public HttpHeaderInvalidHeaderNameException(String message, Throwable cause) {
        super(message, cause);
    }
}