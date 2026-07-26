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
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.RouteMatchedHandler;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteMatchedHandler}. */
final class RouteMatchedHandlerTest {

    static final ServiceResponseContract SHORT_CIRCUIT = ServiceResponse.unimplemented("sc");
    static final RouteContract ROUTE = new Route("/pkg.A/M", (c, r) -> ServiceResponse.ok());

    static boolean passThroughRan;

    static final class PassThrough implements RouteMatchedMiddlewareContract {
        @Override
        public RouteMatchedResult routeMatched(
                ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler) {
            passThroughRan = true;
            return handler.routeMatched(call, route);
        }
    }

    static final class ShortCircuit implements RouteMatchedMiddlewareContract {
        @Override
        public RouteMatchedResult routeMatched(
                ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler) {
            return new RouteMatchedResult(route, SHORT_CIRCUIT);
        }
    }

    static final class CancelThenContinue implements RouteMatchedMiddlewareContract {
        @Override
        public RouteMatchedResult routeMatched(
                ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler) {
            ((CancellationToken) call.getCancellation()).cancel(CancellationReason.CLIENT_CANCELLED);
            return new RouteMatchedResult(route, null);
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
    void emptyChainReturnsRouteWithNoResponse() {
        RouteMatchedHandler handler = new RouteMatchedHandler(new Container());
        RouteMatchedResult result = handler.routeMatched(call(new CancellationToken()), ROUTE);
        assertSame(ROUTE, result.route());
        assertNull(result.response());
    }

    @Test
    void passThroughReachesTerminal() {
        passThroughRan = false;
        RouteMatchedHandler handler =
                new RouteMatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        RouteMatchedResult result = handler.routeMatched(call(new CancellationToken()), ROUTE);
        assertTrue(passThroughRan);
        assertNull(result.response());
    }

    @Test
    void shortCircuitReturnsResponse() {
        RouteMatchedHandler handler =
                new RouteMatchedHandler(containerWith(new ShortCircuit()), ShortCircuit.class);
        RouteMatchedResult result = handler.routeMatched(call(new CancellationToken()), ROUTE);
        assertSame(SHORT_CIRCUIT, result.response());
    }

    @Test
    void preCheckCancellationSkipsMiddleware() {
        passThroughRan = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        RouteMatchedHandler handler =
                new RouteMatchedHandler(containerWith(new PassThrough()), PassThrough.class);
        RouteMatchedResult result = handler.routeMatched(call(token), ROUTE);
        assertEquals(StatusCode.CANCELLED, result.response().getStatus().getCode());
        assertFalse(passThroughRan);
    }

    @Test
    void postCheckCancellationOverridesResult() {
        RouteMatchedHandler handler =
                new RouteMatchedHandler(
                        containerWith(new CancelThenContinue()), CancelThenContinue.class);
        RouteMatchedResult result = handler.routeMatched(call(new CancellationToken()), ROUTE);
        assertEquals(StatusCode.CANCELLED, result.response().getStatus().getCode());
    }
}
