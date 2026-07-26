/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.routing.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcRoutingInvalidMethodException}. */
final class GrpcRoutingInvalidMethodExceptionTest {

    @Test
    void messageConstructor() {
        GrpcRoutingInvalidMethodException ex = new GrpcRoutingInvalidMethodException("bad");
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new IllegalStateException("root");
        GrpcRoutingInvalidMethodException ex = new GrpcRoutingInvalidMethodException("bad", cause);
        assertEquals("bad", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
