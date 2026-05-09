/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.throwable.exception;

public class HttpHeaderUnsupportedOffsetSetException extends HttpHeaderUnsupportedMethodException {

    public HttpHeaderUnsupportedOffsetSetException(String message) {
        super(message);
    }

    public HttpHeaderUnsupportedOffsetSetException(String message, Throwable cause) {
        super(message, cause);
    }
}