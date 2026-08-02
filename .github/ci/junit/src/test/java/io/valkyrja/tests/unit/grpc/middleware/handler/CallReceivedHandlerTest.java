/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.CallReceivedMiddlewareContract;
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.CallReceivedHandler;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link CallReceivedHandler}. */
final class CallReceivedHandlerTest {

    static final ServiceResponseContract SHORT_CIRCUIT = ServiceResponse.unimplemented("sc");

    static boolean passThroughRan;

    static final class PassThrough implements CallReceivedMiddlewareContract {
        @Override
        public CallReceivedResult callReceived(
                ServiceCallContract call, CallReceivedHandlerContract handler) {
            passThroughRan = true;
            return handler.callReceived(call);
        }
    }

    static final class ShortCircuit implements CallReceivedMiddlewareContract {
        @Override
        public CallReceivedResult callReceived(
                ServiceCallContract call, CallReceivedHandlerContract handler) {
            return new CallReceivedResult(call, SHORT_CIRCUIT);
        }
    }

    static final class CancelThenContinue implements CallReceivedMiddlewareContract {
        @Override
        public CallReceivedResult callReceived(
                ServiceCallContract call, CallReceivedHandlerContract handler) {
            ((CancellationToken) call.getCancellation())
                    .cancel(CancellationReason.DEADLINE_EXCEEDED);
            return new CallReceivedResult(call, null);
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

    private ContainerContract containerWith(Object... instances) {
        ContainerContract container = new Container();
        for (Object instance : instances) {
            registerInstance(container, instance);
        }
        return container;
    }

    @SuppressWarnings("unchecked")
    private <T> void registerInstance(ContainerContract container, T instance) {
        container.setSingleton((Class<T>) instance.getClass(), instance);
    }

    @Test
    void emptyChainReturnsCallWithNoResponse() {
        CallReceivedHandler handler = new CallReceivedHandler(new Container());
        ServiceCallContract call = call(new CancellationToken());
        CallReceivedResult result = handler.callReceived(call);
        assertSame(call, result.call());
        assertNull(result.response());
    }

    @Test
    void passThroughChainReachesTerminal() {
        passThroughRan = false;
        ContainerContract container = containerWith(new PassThrough());
        CallReceivedHandler handler = new CallReceivedHandler(container, PassThrough.class);
        CallReceivedResult result = handler.callReceived(call(new CancellationToken()));
        assertTrue(passThroughRan);
        assertNull(result.response());
    }

    @Test
    void shortCircuitStopsChain() {
        passThroughRan = false;
        ContainerContract container = containerWith(new ShortCircuit(), new PassThrough());
        CallReceivedHandler handler =
                new CallReceivedHandler(container, ShortCircuit.class, PassThrough.class);
        CallReceivedResult result = handler.callReceived(call(new CancellationToken()));
        assertSame(SHORT_CIRCUIT, result.response());
        assertFalse(passThroughRan);
    }

    @Test
    void preCheckCancellationSkipsMiddleware() {
        passThroughRan = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        ContainerContract container = containerWith(new PassThrough());
        CallReceivedHandler handler = new CallReceivedHandler(container, PassThrough.class);
        CallReceivedResult result = handler.callReceived(call(token));
        assertEquals(StatusCode.CANCELLED, result.response().getStatus().getCode());
        assertFalse(passThroughRan);
    }

    @Test
    void addAppendsMiddlewareAfterConstruction() {
        passThroughRan = false;
        ContainerContract container = containerWith(new PassThrough());
        CallReceivedHandler handler = new CallReceivedHandler(container);
        handler.add(PassThrough.class);
        handler.callReceived(call(new CancellationToken()));
        assertTrue(passThroughRan);
    }

    @Test
    void postCheckCancellationOverridesResult() {
        ContainerContract container = containerWith(new CancelThenContinue());
        CallReceivedHandler handler = new CallReceivedHandler(container, CancelThenContinue.class);
        CallReceivedResult result = handler.callReceived(call(new CancellationToken()));
        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.response().getStatus().getCode());
    }
}
