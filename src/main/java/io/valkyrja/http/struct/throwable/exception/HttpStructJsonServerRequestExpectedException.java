/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.throwable.exception;

import io.valkyrja.http.struct.throwable.exception.abstract_.HttpStructInvalidArgumentException;

public class HttpStructJsonServerRequestExpectedException extends HttpStructInvalidArgumentException {

    public HttpStructJsonServerRequestExpectedException(String message) {
        super(message);
    }

    public HttpStructJsonServerRequestExpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}