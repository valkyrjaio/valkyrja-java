/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
