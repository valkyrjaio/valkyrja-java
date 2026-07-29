/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoOutputDispatchException;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingNoOutputDispatchException}. */
final class CliRoutingNoOutputDispatchExceptionTest {

    @Test
    void messageConstructor() {
        var exception = new CliRoutingNoOutputDispatchException("message");

        assertEquals("message", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new IllegalStateException("cause");
        var exception = new CliRoutingNoOutputDispatchException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
