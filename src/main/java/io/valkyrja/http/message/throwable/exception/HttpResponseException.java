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
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;

public class HttpResponseException extends HttpMessageRuntimeException {

    protected StatusCode statusCode;
    protected HeaderCollectionContract headers;
    protected ResponseContract response;

    public HttpResponseException(StatusCode statusCode, String message, HeaderCollectionContract headers, ResponseContract response) {
        super(message != null ? message : "");

        this.statusCode = statusCode != null
                ? statusCode
                : (response != null ? response.getStatusCode() : StatusCode.INTERNAL_SERVER_ERROR);
        this.headers    = headers != null ? headers : new HeaderCollection();
        this.response   = response != null ? response.withStatusCode(this.statusCode) : null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public HeaderCollectionContract getHeaders() {
        return headers;
    }

    public ResponseContract getResponse() {
        return response;
    }
}
