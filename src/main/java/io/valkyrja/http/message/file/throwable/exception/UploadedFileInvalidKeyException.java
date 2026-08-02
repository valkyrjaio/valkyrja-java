/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.file.throwable.exception;

import io.valkyrja.http.message.file.throwable.exception.abstract_.UploadedFileInvalidArgumentException;

public class UploadedFileInvalidKeyException extends UploadedFileInvalidArgumentException {

    public UploadedFileInvalidKeyException(String message) {
        super(message);
    }

    public UploadedFileInvalidKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
