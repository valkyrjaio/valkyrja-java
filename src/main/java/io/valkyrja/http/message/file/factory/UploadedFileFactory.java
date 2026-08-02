/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.file.factory;

import io.valkyrja.http.message.file.UploadedFile;
import io.valkyrja.http.message.stream.contract.StreamContract;

public abstract class UploadedFileFactory {

    public static UploadedFile createFromFile(
            String file, int size, String fileName, String mediaType) {
        return new UploadedFile(file, null, size, fileName, mediaType);
    }

    public static UploadedFile createFromStream(
            StreamContract stream, int size, String fileName, String mediaType) {
        return new UploadedFile(null, stream, size, fileName, mediaType);
    }
}
