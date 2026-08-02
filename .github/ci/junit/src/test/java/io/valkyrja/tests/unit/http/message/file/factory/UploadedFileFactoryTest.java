/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.file.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.file.factory.UploadedFileFactory;
import io.valkyrja.http.message.stream.Stream;
import org.junit.jupiter.api.Test;

/** Test the {@link UploadedFileFactory}. */
final class UploadedFileFactoryTest {

    @Test
    void createFromStream() {
        var stream = new Stream();
        var file = UploadedFileFactory.createFromStream(stream, 10, "a.txt", "text/plain");

        assertEquals(10, file.getSize());
        assertEquals("a.txt", file.getClientFilename());
        assertSame(stream, file.getStream());
    }

    @Test
    void createFromFile() {
        var file = UploadedFileFactory.createFromFile("/tmp/a.txt", 5, "a.txt", "text/plain");

        assertEquals(5, file.getSize());
        assertEquals("text/plain", file.getClientMediaType());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new UploadedFileFactory() {});
    }
}
