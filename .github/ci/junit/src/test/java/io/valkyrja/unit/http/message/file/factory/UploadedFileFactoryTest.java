/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.file.factory;

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
