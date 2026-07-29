/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.grpc.middleware.handler;

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
import io.valkyrja.grpc.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteNotMatchedHandler}. */
final class RouteNotMatchedHandlerTest {

    static final ServiceResponseContract REPLACEMENT =
            ServiceResponse.of(io.valkyrja.grpc.message.status.Status.notFound(null));

    static boolean passThroughRan;

    static final class PassThrough implements RouteNotMatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeNotMatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteNotMatchedHandlerContract handler) {
            passThroughRan = true;
            return handler.routeNotMatched(call, response);
        }
    }

    static final class Replace implements RouteNotMatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeNotMatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteNotMatchedHandlerContract handler) {
            return REPLACEMENT;
        }
    }

    static final class CancelThenReturn implements RouteNotMatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeNotMatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteNotMatchedHandlerContract handler) {
            ((CancellationToken) call.getCancellation())
                    .cancel(CancellationReason.DEADLINE_EXCEEDED);
            return response;
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
    void emptyChainReturnsResponseUnchanged() {
        RouteNotMatchedHandler handler = new RouteNotMatchedHandler(new Container());
        ServiceResponseContract response = ServiceResponse.unimplemented();
        assertSame(response, handler.routeNotMatched(call(new CancellationToken()), response));
    }

    @Test
    void passThroughReachesTerminal() {
        passThroughRan = false;
        RouteNotMatchedHandler handler =
                new RouteNotMatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract response = ServiceResponse.unimplemented();
        assertSame(response, handler.routeNotMatched(call(new CancellationToken()), response));
        assertTrue(passThroughRan);
    }

    @Test
    void middlewareCanReplaceResponse() {
        RouteNotMatchedHandler handler =
                new RouteNotMatchedHandler(containerWith(new Replace()), Replace.class);
        ServiceResponseContract result =
                handler.routeNotMatched(
                        call(new CancellationToken()), ServiceResponse.unimplemented());
        assertSame(REPLACEMENT, result);
    }

    @Test
    void preCheckCancellationSkipsMiddleware() {
        passThroughRan = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        RouteNotMatchedHandler handler =
                new RouteNotMatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract result =
                handler.routeNotMatched(call(token), ServiceResponse.unimplemented());
        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
        assertFalse(passThroughRan);
    }

    @Test
    void postCheckCancellationOverlaysReturnedResponse() {
        RouteNotMatchedHandler handler =
                new RouteNotMatchedHandler(
                        containerWith(new CancelThenReturn()), CancelThenReturn.class);
        ServiceResponseContract result =
                handler.routeNotMatched(
                        call(new CancellationToken()), ServiceResponse.unimplemented());
        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.getStatus().getCode());
    }
}
