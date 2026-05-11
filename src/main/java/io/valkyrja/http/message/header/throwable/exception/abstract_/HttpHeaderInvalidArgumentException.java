/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.throwable.exception.abstract_;

import io.valkyrja.http.message.header.throwable.contract.HttpHeaderThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;

public abstract class HttpHeaderInvalidArgumentException extends HttpMessageInvalidArgumentException
        implements HttpHeaderThrowable {

    protected HttpHeaderInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpHeaderInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
