/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import org.junit.jupiter.api.Test;

/** Test the {@link CancelledException}. */
final class CancelledExceptionTest {

    @Test
    void carriesMessageAndReason() {
        CancelledException ex =
                new CancelledException("stop", CancellationReason.DEADLINE_EXCEEDED);
        assertEquals("stop", ex.getMessage());
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, ex.getReason());
    }

    @Test
    void reasonIsNullWhenUnspecified() {
        CancelledException ex = new CancelledException("stop");
        assertEquals("stop", ex.getMessage());
        assertNull(ex.getReason());
    }
}
