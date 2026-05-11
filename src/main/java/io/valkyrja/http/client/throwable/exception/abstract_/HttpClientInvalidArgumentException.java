/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.client.throwable.exception.abstract_;

import io.valkyrja.http.client.throwable.contract.HttpClientThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpInvalidArgumentException;

public abstract class HttpClientInvalidArgumentException extends HttpInvalidArgumentException
        implements HttpClientThrowable {

    protected HttpClientInvalidArgumentException(String message) {
        super(message);
    }

    protected HttpClientInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
