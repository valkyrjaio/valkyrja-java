/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream.throwable.exception.abstract_;

import io.valkyrja.http.message.stream.throwable.contract.HttpStreamThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;

public abstract class HttpStreamInvalidArgumentException extends HttpMessageInvalidArgumentException
        implements HttpStreamThrowable {

    protected HttpStreamInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpStreamInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}