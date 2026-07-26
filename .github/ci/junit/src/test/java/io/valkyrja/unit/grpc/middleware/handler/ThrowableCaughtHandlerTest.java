/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import io.valkyrja.grpc.message.status.Status;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link ThrowableCaughtHandler}. */
final class ThrowableCaughtHandlerTest {

    static final Throwable THROWABLE = new IllegalStateException("boom");
    static final ServiceResponseContract MAPPED = ServiceResponse.of(Status.internal(null));

    static boolean passThroughRan;

    static final class PassThrough implements ThrowableCaughtMiddlewareContract {
        @Override
        public ServiceResponseContract throwableCaught(
                ServiceCallContract call,
                ServiceResponseContract response,
                Throwable throwable,
                ThrowableCaughtHandlerContract handler) {
            passThroughRan = true;
            return handler.throwableCaught(call, response, throwable);
        }
    }

    static final class MapThrowable implements ThrowableCaughtMiddlewareContract {
        @Override
        public ServiceResponseContract throwableCaught(
                ServiceCallContract call,
                ServiceResponseContract response,
                Throwable throwable,
                ThrowableCaughtHandlerContract handler) {
            return MAPPED;
        }
    }

    static final class CancelThenReturn implements ThrowableCaughtMiddlewareContract {
        @Override
        public ServiceResponseContract throwableCaught(
                ServiceCallContract call,
                ServiceResponseContract response,
                Throwable throwable,
                ThrowableCaughtHandlerContract handler) {
            ((CancellationToken) call.getCancellation()).cancel(CancellationReason.DEADLINE_EXCEEDED);
            return response;
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
    void emptyChainReturnsResponseUnchanged() {
        ThrowableCaughtHandler handler = new ThrowableCaughtHandler(new Container());
        ServiceResponseContract response = ServiceResponse.of(Status.internal(null));
        assertSame(
                response,
                handler.throwableCaught(call(new CancellationToken()), response, THROWABLE));
    }

    @Test
    void passThroughReachesTerminal() {
        passThroughRan = false;
        ThrowableCaughtHandler handler =
                new ThrowableCaughtHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract response = ServiceResponse.of(Status.internal(null));
        assertSame(
                response,
                handler.throwableCaught(call(new CancellationToken()), response, THROWABLE));
        assertTrue(passThroughRan);
    }

    @Test
    void middlewareCanMapThrowableToResponse() {
        ThrowableCaughtHandler handler =
                new ThrowableCaughtHandler(containerWith(new MapThrowable()), MapThrowable.class);
        assertSame(
                MAPPED,
                handler.throwableCaught(
                        call(new CancellationToken()), ServiceResponse.ok(), THROWABLE));
    }

    @Test
    void preCheckCancellationSkipsMiddleware() {
        passThroughRan = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        ThrowableCaughtHandler handler =
                new ThrowableCaughtHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract result =
                handler.throwableCaught(call(token), ServiceResponse.ok(), THROWABLE);
        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
        assertFalse(passThroughRan);
    }

    @Test
    void postCheckCancellationOverlaysReturnedResponse() {
        ThrowableCaughtHandler handler =
                new ThrowableCaughtHandler(
                        containerWith(new CancelThenReturn()), CancelThenReturn.class);
        ServiceResponseContract result =
                handler.throwableCaught(call(new CancellationToken()), ServiceResponse.ok(), THROWABLE);
        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.getStatus().getCode());
    }
}
