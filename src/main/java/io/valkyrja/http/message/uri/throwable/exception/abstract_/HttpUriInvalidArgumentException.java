/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.throwable.exception.abstract_;

import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;
import io.valkyrja.http.message.uri.throwable.contract.HttpUriThrowable;

public abstract class HttpUriInvalidArgumentException extends HttpMessageInvalidArgumentException
        implements HttpUriThrowable {

    protected HttpUriInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpUriInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}