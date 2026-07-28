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
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteDispatchedHandler}. */
final class RouteDispatchedHandlerTest {

    static final RouteContract ROUTE = new Route("/pkg.A/M", (c, r) -> ServiceResponse.ok());
    static final ServiceResponseContract REPLACEMENT = ServiceResponse.ok("replaced");

    static boolean passThroughRan;

    static final class PassThrough implements RouteDispatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeDispatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteContract route,
                RouteDispatchedHandlerContract handler) {
            passThroughRan = true;
            return handler.routeDispatched(call, response, route);
        }
    }

    static final class Replace implements RouteDispatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeDispatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteContract route,
                RouteDispatchedHandlerContract handler) {
            return REPLACEMENT;
        }
    }

    static final class CancelThenReturn implements RouteDispatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeDispatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteContract route,
                RouteDispatchedHandlerContract handler) {
            ((CancellationToken) call.getCancellation())
                    .cancel(CancellationReason.CLIENT_CANCELLED);
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
        RouteDispatchedHandler handler = new RouteDispatchedHandler(new Container());
        ServiceResponseContract response = ServiceResponse.ok();
        assertSame(
                response, handler.routeDispatched(call(new CancellationToken()), response, ROUTE));
    }

    @Test
    void passThroughReachesTerminal() {
        passThroughRan = false;
        RouteDispatchedHandler handler =
                new RouteDispatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract response = ServiceResponse.ok();
        assertSame(
                response, handler.routeDispatched(call(new CancellationToken()), response, ROUTE));
        assertTrue(passThroughRan);
    }

    @Test
    void middlewareCanReplaceResponse() {
        RouteDispatchedHandler handler =
                new RouteDispatchedHandler(containerWith(new Replace()), Replace.class);
        assertSame(
                REPLACEMENT,
                handler.routeDispatched(
                        call(new CancellationToken()), ServiceResponse.ok(), ROUTE));
    }

    @Test
    void preCheckCancellationSkipsMiddleware() {
        passThroughRan = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        RouteDispatchedHandler handler =
                new RouteDispatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        ServiceResponseContract result =
                handler.routeDispatched(call(token), ServiceResponse.ok(), ROUTE);
        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
        assertFalse(passThroughRan);
    }

    @Test
    void postCheckCancellationOverlaysReturnedResponse() {
        RouteDispatchedHandler handler =
                new RouteDispatchedHandler(
                        containerWith(new CancelThenReturn()), CancelThenReturn.class);
        ServiceResponseContract result =
                handler.routeDispatched(call(new CancellationToken()), ServiceResponse.ok(), ROUTE);
        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
    }
}
