/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.TerminatedHandler;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link TerminatedHandler} — an always-run stage with no cancellation short-circuit. */
final class TerminatedHandlerTest {

    static int ranCount;

    static final class Recording implements TerminatedMiddlewareContract {
        @Override
        public void terminated(
                ServiceCallContract call,
                ServiceResponseContract response,
                TerminatedHandlerContract handler) {
            ranCount++;
            handler.terminated(call, response);
        }
    }

    private ServiceCallContract call(CancellationToken token) {
        return new ServiceCall(
                "/pkg.A/M", new Metadata(), Deadline.none(), token, Peer.insecure("x"), List.of(), null);
    }

    @SuppressWarnings("unchecked")
    private ContainerContract containerWith(Object instance) {
        ContainerContract container = new Container();
        container.setSingleton((Class<Object>) instance.getClass(), instance);
        return container;
    }

    @Test
    void emptyChainDoesNothing() {
        TerminatedHandler handler = new TerminatedHandler(new Container());
        assertDoesNotThrow(
                () -> handler.terminated(call(new CancellationToken()), ServiceResponse.ok()));
    }

    @Test
    void passThroughRunsMiddleware() {
        ranCount = 0;
        TerminatedHandler handler =
                new TerminatedHandler(containerWith(new Recording()), Recording.class);
        handler.terminated(call(new CancellationToken()), ServiceResponse.ok());
        assertTrue(ranCount > 0);
    }

    @Test
    void runsEvenWhenCallIsCancelled() {
        ranCount = 0;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        TerminatedHandler handler =
                new TerminatedHandler(containerWith(new Recording()), Recording.class);
        handler.terminated(call(token), ServiceResponse.ok());
        assertTrue(ranCount > 0);
    }
}
