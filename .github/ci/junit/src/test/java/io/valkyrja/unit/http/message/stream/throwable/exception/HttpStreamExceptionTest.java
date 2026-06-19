/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.stream.throwable.exception.HttpStreamInvalidLengthException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamInvalidStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamNoStreamAvailableException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamReadException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamSeekException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamTellException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamWriteException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnreadableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnseekableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnwritableStreamException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the http stream throwables and their abstract argument/runtime bases. */
final class HttpStreamExceptionTest {

    private final List<Function<String, ? extends RuntimeException>> messageCtors =
            List.of(
                    HttpStreamInvalidLengthException::new,
                    HttpStreamInvalidStreamException::new,
                    HttpStreamNoStreamAvailableException::new,
                    HttpStreamStreamReadException::new,
                    HttpStreamStreamSeekException::new,
                    HttpStreamStreamTellException::new,
                    HttpStreamStreamWriteException::new,
                    HttpStreamUnreadableStreamException::new,
                    HttpStreamUnseekableStreamException::new,
                    HttpStreamUnwritableStreamException::new);

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> causeCtors =
            List.of(
                    HttpStreamInvalidLengthException::new,
                    HttpStreamInvalidStreamException::new,
                    HttpStreamNoStreamAvailableException::new,
                    HttpStreamStreamReadException::new,
                    HttpStreamStreamSeekException::new,
                    HttpStreamStreamTellException::new,
                    HttpStreamStreamWriteException::new,
                    HttpStreamUnreadableStreamException::new,
                    HttpStreamUnseekableStreamException::new,
                    HttpStreamUnwritableStreamException::new);

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