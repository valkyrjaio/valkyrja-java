/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.throwable.exception;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageRuntimeException;
import org.jspecify.annotations.Nullable;

public class HttpResponseException extends HttpMessageRuntimeException {

    protected StatusCode statusCode;
    protected HeaderCollectionContract headers;
    protected @Nullable ResponseContract response;

    public HttpResponseException(
            StatusCode statusCode,
            String message,
            HeaderCollectionContract headers,
            @Nullable ResponseContract response) {
        super(message != null ? message : "");

        this.statusCode =
                statusCode != null
                        ? statusCode
                        : (response != null
                                ? response.getStatusCode()
                                : StatusCode.INTERNAL_SERVER_ERROR);
        this.headers = headers != null ? headers : new HeaderCollection();
        this.response = response != null ? response.withStatusCode(this.statusCode) : null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public HeaderCollectionContract getHeaders() {
        return headers;
    }

    public @Nullable ResponseContract getResponse() {
        return response;
    }
}
