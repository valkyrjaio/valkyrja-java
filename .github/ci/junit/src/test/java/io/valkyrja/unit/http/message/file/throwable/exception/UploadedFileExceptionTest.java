/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.file.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.file.throwable.exception.UploadedFileAlreadyMovedException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidDirectoryException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidFilesArrayStructureException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidKeyException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidParamException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidTmpNameException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidUploadErrorException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidUploadedFileException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidValueException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileMoveFailureException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileUnableToWriteFileException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileUploadErrorException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the uploaded-file throwables and their abstract argument/runtime bases. */
final class UploadedFileExceptionTest {

    private final List<Function<String, ? extends RuntimeException>> messageCtors =
            List.of(
                    UploadedFileAlreadyMovedException::new,
                    UploadedFileInvalidDirectoryException::new,
                    UploadedFileInvalidFilesArrayStructureException::new,
                    UploadedFileInvalidKeyException::new,
                    UploadedFileInvalidParamException::new,
                    UploadedFileInvalidTmpNameException::new,
                    UploadedFileInvalidUploadErrorException::new,
                    UploadedFileInvalidUploadedFileException::new,
                    UploadedFileInvalidValueException::new,
                    UploadedFileMoveFailureException::new,
                    UploadedFileUnableToWriteFileException::new,
                    UploadedFileUploadErrorException::new);

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> causeCtors =
            List.of(
                    UploadedFileAlreadyMovedException::new,
                    UploadedFileInvalidDirectoryException::new,
                    UploadedFileInvalidFilesArrayStructureException::new,
                    UploadedFileInvalidKeyException::new,
                    UploadedFileInvalidParamException::new,
                    UploadedFileInvalidTmpNameException::new,
                    UploadedFileInvalidUploadErrorException::new,
                    UploadedFileInvalidUploadedFileException::new,
                    UploadedFileInvalidValueException::new,
                    UploadedFileMoveFailureException::new,
                    UploadedFileUnableToWriteFileException::new,
                    UploadedFileUploadErrorException::new);

    @Test
    void messageConstructors() {
        for (var ctor : messageCtors) {
            assertEquals("m", ctor.apply("m").getMessage());
        }
    }

    @Test
    void messageAndCauseConstructors() {
        var cause = new IllegalStateException("cause");

        for (var ctor : causeCtors) {
            var exception = ctor.apply("m", cause);
            assertEquals("m", exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }
}
