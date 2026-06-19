/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidDynamicRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidMethodTypeException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidParameterRegexException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteParameterException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteRegexException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoCastException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoRequestStructException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoResponseStructException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the http routing throwables and their abstract argument/runtime bases. */
final class HttpRoutingExceptionTest {

    private final List<Function<String, ? extends RuntimeException>> messageCtors =
            List.of(
                    HttpRoutingInvalidDynamicRouteNameException::new,
                    HttpRoutingInvalidMethodTypeException::new,
                    HttpRoutingInvalidParameterRegexException::new,
                    HttpRoutingInvalidRouteNameException::new,
                    HttpRoutingInvalidRouteParameterException::new,
                    HttpRoutingInvalidRoutePathException::new,
                    HttpRoutingInvalidRouteRegexException::new,
                    HttpRoutingNoCastException::new,
                    HttpRoutingNoRequestStructException::new,
                    HttpRoutingNoResponseStructException::new);

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> causeCtors =
            List.of(
                    HttpRoutingInvalidDynamicRouteNameException::new,
                    HttpRoutingInvalidMethodTypeException::new,
                    HttpRoutingInvalidParameterRegexException::new,
                    HttpRoutingInvalidRouteNameException::new,
                    HttpRoutingInvalidRouteParameterException::new,
                    HttpRoutingInvalidRoutePathException::new,
                    HttpRoutingInvalidRouteRegexException::new,
                    HttpRoutingNoCastException::new,
                    HttpRoutingNoRequestStructException::new,
                    HttpRoutingNoResponseStructException::new);

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
