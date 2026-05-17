/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file;

import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileAlreadyMovedException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidDirectoryException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidUploadedFileException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileMoveFailureException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileUnableToWriteFileException;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jspecify.annotations.Nullable;

public class UploadedFile implements UploadedFileContract {

    protected boolean hasBeenMoved = false;

    protected @Nullable String file;
    protected @Nullable StreamContract stream;
    protected int size;
    protected String fileName;
    protected String mediaType;

    public UploadedFile(
            @Nullable String file,
            @Nullable StreamContract stream,
            int size,
            @Nullable String fileName,
            @Nullable String mediaType) {
        if (file == null && stream == null) {
            throw new UploadedFileInvalidUploadedFileException(
                    "One of file or stream are required");
        }

        this.file = file;
        this.stream = stream;
        this.size = size;
        this.fileName = fileName != null ? fileName : "";
        this.mediaType = mediaType != null ? mediaType : "";
    }

    @Override
    public StreamContract getStream() {
        validateHasNotBeenMoved("Cannot retrieve stream after it has already been moved");

        if (this.stream != null) {
            return this.stream;
        }

        if (this.file == null) {
            throw new UploadedFileInvalidUploadedFileException(
                    "One of file or stream are required");
        }

        this.stream = new Stream();

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(this.file));
            this.stream.write(new String(bytes, StandardCharsets.UTF_8));
            this.stream.rewind();
        } catch (IOException e) {
            throw new UploadedFileInvalidUploadedFileException("Unable to read file: " + this.file);
        }

        return this.stream;
    }

    @Override
    public void moveTo(String targetPath) {
        validateHasNotBeenMoved();

        String targetDirectory = getDirectoryName(targetPath);

        validateMoveToTargetDirectory(targetDirectory);

        writeStream(targetPath);

        if (this.stream != null) {
            this.stream.close();
        }

        if (this.file != null && new File(this.file).isFile()) {
            try {
                Files.delete(Paths.get(this.file));
            } catch (IOException e) {
                throw new UploadedFileMoveFailureException(
                        "Unable to delete original file: " + this.file);
            }
        }

        this.hasBeenMoved = true;
    }

    @Override
    public boolean hasSize() {
        return this.size != 0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public boolean hasClientFilename() {
        return !this.fileName.isEmpty();
    }

    @Override
    public String getClientFilename() {
        return this.fileName;
    }

    @Override
    public boolean hasClientMediaType() {
        return !this.mediaType.isEmpty();
    }

    @Override
    public String getClientMediaType() {
        return this.mediaType;
    }

    protected void validateHasNotBeenMoved() {
        validateHasNotBeenMoved(null);
    }

    protected void validateHasNotBeenMoved(@Nullable String message) {
        if (this.hasBeenMoved) {
            throw new UploadedFileAlreadyMovedException(
                    message != null ? message : "Cannot move file after it has already been moved");
        }
    }

    protected void validateMoveToTargetDirectory(String targetDirectory) {
        File dir = new File(targetDirectory);

        if (!dir.isDirectory() || !dir.canWrite()) {
            throw new UploadedFileInvalidDirectoryException(
                    "The target directory `"
                            + targetDirectory
                            + "` does not exists or is not writable");
        }
    }

    protected void writeStream(String path) {
        try (FileOutputStream out = new FileOutputStream(path)) {
            StreamContract s = getStream();
            s.rewind();

            while (!s.eof()) {
                String chunk = s.read(4096);
                out.write(chunk.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new UploadedFileUnableToWriteFileException("Unable to write to designated path");
        }
    }

    protected String getDirectoryName(String path) {
        Path p = Paths.get(path);
        Path parent = p.getParent();
        return parent != null ? parent.toString() : ".";
    }
}
