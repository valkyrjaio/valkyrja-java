/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.http.message.stream.Stream;
import org.junit.jupiter.api.Test;

/** Exercises {@link UploadedFile}'s protected {@code getDirectoryName} branches directly. */
final class UploadedFileInternalsTest {

    private final UploadedFile file = new UploadedFile(null, new Stream(), 0, null, null);

    @Test
    void getDirectoryNameReturnsParentOrCurrentDirectory() {
        assertEquals("/dir", file.getDirectoryName("/dir/file.txt"));
        assertEquals(".", file.getDirectoryName("file.txt"));
    }
}
