/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.support.Cancellation;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link Cancellation} two-question check. */
final class CancellationTest {

    private static ServiceCall call(CancellationToken token) {
        return new ServiceCall(
                "/pkg.A/M", new Metadata(), Deadline.none(), token, Peer.insecure("x"), List.of(), null);
    }

    @Test
    void returnsNullWhenNotCancelledAndNoResponse() {
        assertNull(Cancellation.checkAndFinalize(call(new CancellationToken()), null));
    }

    @Test
    void returnsNullWhenNotCancelledAndResponseIsHealthy() {
        assertNull(
                Cancellation.checkAndFinalize(call(new CancellationToken()), ServiceResponse.ok()));
    }

    @Test
    void buildsFreshCancellationWhenCancelledAndNoResponse() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        ServiceResponseContract result = Cancellation.checkAndFinalize(call(token), null);
        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.getStatus().getCode());
    }

    @Test
    void overlaysCancellationOntoExistingResponsePreservingMetadata() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        ServiceResponseContract existing =
                ServiceResponse.ok().withInitialMetadata(new Metadata().with("k", "v"));

        ServiceResponseContract result = Cancellation.checkAndFinalize(call(token), existing);
        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
        assertEquals("v", result.getInitialMetadata().get("k"));
    }

    @Test
    void passesThroughAlreadyCancelledResponse() {
        ServiceResponseContract cancelled = ServiceResponse.cancelled(CancellationReason.CLIENT_CANCELLED);
        ServiceResponseContract result =
                Cancellation.checkAndFinalize(call(new CancellationToken()), cancelled);
        assertSame(cancelled, result);
    }
}
