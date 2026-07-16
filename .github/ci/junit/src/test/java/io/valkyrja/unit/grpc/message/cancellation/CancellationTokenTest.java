/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.message.cancellation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Test the {@link CancellationToken}. */
final class CancellationTokenTest {

    @Test
    void notCancelledByDefault() {
        CancellationToken token = new CancellationToken();
        assertFalse(token.isCancelled());
        assertNull(token.getReason());
        assertDoesNotThrow(token::throwIfCancelled);
    }

    @Test
    void neverSentinelNeverCancels() {
        CancellationToken token = CancellationToken.never();
        assertFalse(token.isCancelled());
        assertNull(token.getReason());
    }

    @Test
    void cancelSetsStateAndReason() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        assertTrue(token.isCancelled());
        assertEquals(CancellationReason.CLIENT_CANCELLED, token.getReason());
    }

    @Test
    void throwIfCancelledThrowsWithReason() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        CancelledException ex = assertThrows(CancelledException.class, token::throwIfCancelled);
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, ex.getReason());
    }

    @Test
    void listenerFiresOnCancel() {
        CancellationToken token = new CancellationToken();
        AtomicInteger fired = new AtomicInteger();
        token.onCancelled(fired::incrementAndGet);
        assertEquals(0, fired.get());
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        assertEquals(1, fired.get());
    }

    @Test
    void listenerRegisteredAfterCancelFiresImmediately() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        AtomicInteger fired = new AtomicInteger();
        token.onCancelled(fired::incrementAndGet);
        assertEquals(1, fired.get());
    }

    @Test
    void cancelIsIdempotent() {
        CancellationToken token = new CancellationToken();
        AtomicInteger fired = new AtomicInteger();
        token.onCancelled(fired::incrementAndGet);
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        assertEquals(1, fired.get());
        assertEquals(CancellationReason.CLIENT_CANCELLED, token.getReason());
    }
}
