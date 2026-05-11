/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.throwable.exception.abstract_;

import io.valkyrja.http.struct.throwable.contract.HttpStructThrowable;
import io.valkyrja.http.throwable.exception.abstract_.HttpRuntimeException;

public abstract class HttpStructRuntimeException extends HttpRuntimeException
        implements HttpStructThrowable {

    protected HttpStructRuntimeException(String message) {
        super(message);
    }

    protected HttpStructRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
