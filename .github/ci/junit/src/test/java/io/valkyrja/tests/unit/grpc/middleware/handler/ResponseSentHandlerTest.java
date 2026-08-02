/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.middleware.handler;

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
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.ResponseSentHandler;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link ResponseSentHandler} — an always-run stage with no cancellation short-circuit.
 */
final class ResponseSentHandlerTest {

    static int ranCount;

    static final class Recording implements ResponseSentMiddlewareContract {
        @Override
        public void responseSent(
                ServiceCallContract call,
                ServiceResponseContract response,
                ResponseSentHandlerContract handler) {
            ranCount++;
            handler.responseSent(call, response);
        }
    }

    private ServiceCallContract call(CancellationToken token) {
        return new ServiceCall(
                "/pkg.A/M",
                new Metadata(),
                Deadline.none(),
                token,
                Peer.insecure("x"),
                List.of(),
                null);
    }

    @SuppressWarnings("unchecked")
    private ContainerContract containerWith(Object instance) {
        ContainerContract container = new Container();
        container.setSingleton((Class<Object>) instance.getClass(), instance);
        return container;
    }

    @Test
    void emptyChainDoesNothing() {
        ResponseSentHandler handler = new ResponseSentHandler(new Container());
        assertDoesNotThrow(
                () -> handler.responseSent(call(new CancellationToken()), ServiceResponse.ok()));
    }

    @Test
    void passThroughRunsMiddleware() {
        ranCount = 0;
        ResponseSentHandler handler =
                new ResponseSentHandler(containerWith(new Recording()), Recording.class);
        handler.responseSent(call(new CancellationToken()), ServiceResponse.ok());
        assertTrue(ranCount > 0);
    }

    @Test
    void runsEvenWhenCallIsCancelled() {
        ranCount = 0;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        ResponseSentHandler handler =
                new ResponseSentHandler(containerWith(new Recording()), Recording.class);
        handler.responseSent(call(token), ServiceResponse.ok());
        assertTrue(ranCount > 0);
    }
}
