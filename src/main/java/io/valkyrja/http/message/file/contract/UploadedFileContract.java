/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.file.contract;

import io.valkyrja.http.message.stream.contract.StreamContract;

public interface UploadedFileContract {

    StreamContract getStream();

    void moveTo(String targetPath);

    boolean hasSize();

    int getSize();

    boolean hasClientFilename();

    String getClientFilename();

    boolean hasClientMediaType();

    String getClientMediaType();
}
