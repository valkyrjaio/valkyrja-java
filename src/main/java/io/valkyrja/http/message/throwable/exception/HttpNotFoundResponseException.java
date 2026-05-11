/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.throwable.exception;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;

public class HttpNotFoundResponseException extends HttpResponseException {

    public HttpNotFoundResponseException(StatusCode statusCode, String message, HeaderCollectionContract headers) {
        super(statusCode != null ? statusCode : StatusCode.NOT_FOUND, message, headers, null);
    }
}
