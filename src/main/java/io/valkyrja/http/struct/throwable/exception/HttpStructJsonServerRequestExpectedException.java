/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.throwable.exception;

import io.valkyrja.http.struct.throwable.exception.abstract_.HttpStructInvalidArgumentException;

public class HttpStructJsonServerRequestExpectedException
        extends HttpStructInvalidArgumentException {

    public HttpStructJsonServerRequestExpectedException(String message) {
        super(message);
    }

    public HttpStructJsonServerRequestExpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
