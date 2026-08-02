/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.throwable.exception;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;

public class HttpNotFoundResponseException extends HttpResponseException {

    public HttpNotFoundResponseException(
            StatusCode statusCode, String message, HeaderCollectionContract headers) {
        super(statusCode != null ? statusCode : StatusCode.NOT_FOUND, message, headers, null);
    }
}
