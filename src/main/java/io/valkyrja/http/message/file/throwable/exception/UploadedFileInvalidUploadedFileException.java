/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.file.throwable.exception;

import io.valkyrja.http.message.file.throwable.exception.abstract_.UploadedFileInvalidArgumentException;

public class UploadedFileInvalidUploadedFileException extends UploadedFileInvalidArgumentException {

    public UploadedFileInvalidUploadedFileException(String message) {
        super(message);
    }

    public UploadedFileInvalidUploadedFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
