/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.routing.throwable.exception.CliRoutingArgumentValuesValidationException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidArgumentNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidHelpTextCallableException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionWithValueException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidRouteNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoCastException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoHelpTextException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoOutputDispatchException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingOptionValuesValidationException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the cli routing throwables and their abstract argument/runtime bases. */
final class CliRoutingExceptionTest {

    private final List<Function<String, ? extends RuntimeException>> messageCtors =
            List.of(
                    CliRoutingArgumentValuesValidationException::new,
                    CliRoutingInvalidArgumentNameException::new,
                    CliRoutingInvalidHelpTextCallableException::new,
                    CliRoutingInvalidOptionNameException::new,
                    CliRoutingInvalidOptionWithValueException::new,
                    CliRoutingInvalidRouteNameException::new,
                    CliRoutingNoCastException::new,
                    CliRoutingNoHelpTextException::new,
                    CliRoutingNoOutputDispatchException::new,
                    CliRoutingOptionValuesValidationException::new);

    private final List<BiFunction<String, Throwable, ? extends RuntimeException>> causeCtors =
            List.of(
                    CliRoutingArgumentValuesValidationException::new,
                    CliRoutingInvalidArgumentNameException::new,
                    CliRoutingInvalidHelpTextCallableException::new,
                    CliRoutingInvalidOptionNameException::new,
                    CliRoutingInvalidOptionWithValueException::new,
                    CliRoutingInvalidRouteNameException::new,
                    CliRoutingNoCastException::new,
                    CliRoutingNoHelpTextException::new,
                    CliRoutingNoOutputDispatchException::new,
                    CliRoutingOptionValuesValidationException::new);

    @Test
    void messageConstructors() {
        for (var ctor : messageCtors) {
            var exception = ctor.apply("m");
            assertEquals("m", exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
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

    @Test
    void traceCodeIsAvailable() {
        assertTrue(new CliRoutingNoCastException("m").getTraceCode().matches("[0-9a-f]{32}"));
    }
}
