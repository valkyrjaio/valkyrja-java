/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import io.valkyrja.http.message.file.UploadedFile;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileAlreadyMovedException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidDirectoryException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidUploadedFileException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileMoveFailureException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileUnableToWriteFileException;
import io.valkyrja.http.message.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the {@link UploadedFile}. */
final class UploadedFileTest {

    private static Stream streamOf(String content) {
        var stream = new Stream();
        stream.write(content);
        stream.rewind();
        return stream;
    }

    @Test
    void requiresEitherFileOrStream() {
        assertThrows(
                UploadedFileInvalidUploadedFileException.class,
                () -> new UploadedFile(null, null, 0, null, null));
    }

    @Test
    void accessors() {
        var file = new UploadedFile(null, streamOf("data"), 4, "report.txt", "text/plain");

        assertTrue(file.hasSize());
        assertEquals(4, file.getSize());
        assertTrue(file.hasClientFilename());
        assertEquals("report.txt", file.getClientFilename());
        assertTrue(file.hasClientMediaType());
        assertEquals("text/plain", file.getClientMediaType());
    }

    @Test
    void defaultsForEmptyMetadata() {
        var file = new UploadedFile(null, streamOf("data"), 0, null, null);

        assertFalse(file.hasSize());
        assertFalse(file.hasClientFilename());
        assertFalse(file.hasClientMediaType());
    }

    @Test
    void getStreamReturnsProvidedStream() {
        var stream = streamOf("data");
        var file = new UploadedFile(null, stream, 4, null, null);

        assertEquals(stream, file.getStream());
    }

    @Test
    void getStreamReadsFromFile(@TempDir Path dir) throws IOException {
        Path source = Files.writeString(dir.resolve("source.txt"), "file-content");
        var file = new UploadedFile(source.toString(), null, 0, null, null);

        assertEquals("file-content", file.getStream().toString());
    }

    @Test
    void getStreamThrowsForUnreadableFile(@TempDir Path dir) {
        var file = new UploadedFile(dir.resolve("missing.txt").toString(), null, 0, null, null);

        assertThrows(UploadedFileInvalidUploadedFileException.class, file::getStream);
    }

    @Test
    void moveToWritesTargetAndMarksMoved(@TempDir Path dir) throws IOException {
        var file = new UploadedFile(null, streamOf("payload"), 7, null, null);
        Path target = dir.resolve("moved.txt");

        file.moveTo(target.toString());

        assertEquals("payload", Files.readString(target));
        // Operations after a move are rejected.
        assertThrows(UploadedFileAlreadyMovedException.class, file::getStream);
        assertThrows(UploadedFileAlreadyMovedException.class, () -> file.moveTo(target.toString()));
    }

    @Test
    void moveToDeletesOriginalFile(@TempDir Path dir) throws IOException {
        Path source = Files.writeString(dir.resolve("source.txt"), "data");
        var file = new UploadedFile(source.toString(), null, 4, null, null);
        Path target = dir.resolve("dest.txt");

        file.moveTo(target.toString());

        assertEquals("data", Files.readString(target));
        assertFalse(Files.exists(source));
    }

    @Test
    void moveToRejectsInvalidTargetDirectory() {
        var file = new UploadedFile(null, streamOf("data"), 4, null, null);

        assertThrows(
                UploadedFileInvalidDirectoryException.class,
                () -> file.moveTo("/no/such/directory/out.txt"));
    }

    @Test
    void moveToThrowsWhenTargetCannotBeWritten(@TempDir Path dir) throws IOException {
        // The target path is an existing directory, so opening it for writing fails.
        Path target = Files.createDirectory(dir.resolve("subdir"));
        var file = new UploadedFile(null, streamOf("data"), 4, null, null);

        assertThrows(
                UploadedFileUnableToWriteFileException.class, () -> file.moveTo(target.toString()));
    }

    @Test
    void moveToThrowsWhenOriginalCannotBeDeleted(@TempDir Path dir) throws IOException {
        Path source = Files.writeString(dir.resolve("source.txt"), "data");
        var file = new UploadedFile(source.toString(), null, 4, null, null);
        Path target = dir.resolve("dest.txt");
        // Cache the source contents before mocking Files so only the delete is intercepted.
        file.getStream();

        try (var files = mockStatic(Files.class)) {
            files.when(() -> Files.delete(source)).thenThrow(new IOException("locked"));

            assertThrows(
                    UploadedFileMoveFailureException.class, () -> file.moveTo(target.toString()));
        }
    }

    @Test
    void moveToRejectsNonWritableDirectory(@TempDir Path dir) throws IOException {
        Path readonly = Files.createDirectory(dir.resolve("readonly"));
        assertTrue(readonly.toFile().setWritable(false));
        var file = new UploadedFile(null, streamOf("data"), 4, null, null);
        try {
            assertThrows(
                    UploadedFileInvalidDirectoryException.class,
                    () -> file.moveTo(readonly.resolve("out.txt").toString()));
        } finally {
            readonly.toFile().setWritable(true);
        }
    }

    @Test
    void moveToSkipsDeleteWhenOriginalNoLongerExists(@TempDir Path dir) throws IOException {
        var source = Files.writeString(dir.resolve("src.txt"), "data");
        var file = new UploadedFile(source.toString(), null, 4, null, null);
        // Cache the content, then remove the original so the post-move delete is skipped.
        file.getStream();
        Files.delete(source);
        var target = dir.resolve("out.txt");

        file.moveTo(target.toString());

        assertEquals("data", Files.readString(target));
    }
}
