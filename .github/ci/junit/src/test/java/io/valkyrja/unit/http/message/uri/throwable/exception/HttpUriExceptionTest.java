/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidFromStringException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPathException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidQueryException;
import io.valkyrja.http.message.uri.throwable.exception.NoPortExceptionHttpUri;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the http uri throwables and their abstract argument/runtime bases. */
final class HttpUriExceptionTest {

    private final List<Function<String, ? extends RuntimeException>> messageCtors =
            List.of(
                    HttpUriInvalidFromStringException::new,
                    HttpUriInvalidPathException::new,
                    HttpUriInvalidPortException::new,
                    HttpUriInvalidQueryException::new,
                    NoPortExceptionHttpUri::new);

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> causeCtors =
            List.of(
                    HttpUriInvalidFromStringException::new,
                    HttpUriInvalidPathException::new,
                    HttpUriInvalidPortException::new,
                    HttpUriInvalidQueryException::new,
                    NoPortExceptionHttpUri::new);

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
