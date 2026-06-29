/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.support.generator.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.fixtures.support.generator.FileGeneratorClass;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the {@link io.valkyrja.support.generator.abstract_.FileGenerator}. */
final class FileGeneratorTest {

    @Test
    void generateFileWritesContentsAndCreatesParentDirectories(@TempDir Path dir) throws IOException {
        Path target = dir.resolve("nested/output.txt");

        new FileGeneratorClass(target.toString()).generateFile();

        assertTrue(Files.exists(target));
        assertEquals(FileGeneratorClass.CONTENTS, Files.readString(target));
    }

    @Test
    void generateFileThrowsRuntimeExceptionWhenPathIsInvalid(@TempDir Path dir) throws IOException {
        // Create a regular file, then treat it as a parent directory — createDirectories fails.
        Path file = Files.createFile(dir.resolve("a-file"));
        String invalidPath = file.resolve("child.txt").toString();

        var thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> new FileGeneratorClass(invalidPath).generateFile());

        assertTrue(thrown.getMessage().contains("Failed to write file"));
    }
}
