/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.routing.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.collection.RouteCollection;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.dispatcher.Router;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Test the {@link Router}. */
@ExtendWith(MockitoExtension.class)
final class RouterTest {

    private static final String METHOD = "/pkg.Greeter/SayHello";

    @Mock private ContainerContract container;
    @Mock private RouteMatchedHandlerContract routeMatchedHandler;
    @Mock private RouteNotMatchedHandlerContract routeNotMatchedHandler;
    @Mock private RouteDispatchedHandlerContract routeDispatchedHandler;
    @Mock private ThrowableCaughtHandlerContract throwableCaughtHandler;
    @Mock private SendingResponseHandlerContract sendingResponseHandler;
    @Mock private ResponseSentHandlerContract responseSentHandler;

    private final RouteCollection collection = new RouteCollection();
    private Router router;

    @BeforeEach
    void setUp() {
        router =
                new Router(
                        container,
                        collection,
                        routeMatchedHandler,
                        routeNotMatchedHandler,
                        routeDispatchedHandler,
                        throwableCaughtHandler,
                        sendingResponseHandler,
                        responseSentHandler);
    }

    private ServiceCallContract call(CancellationToken token) {
        return new ServiceCall(
                METHOD, new Metadata(), Deadline.none(), token, Peer.insecure("x"), List.of(), null);
    }

    private Route route(java.util.function.BiFunction<ContainerContract, RouteContract, ServiceResponseContract> handler) {
        return new Route(METHOD, handler);
    }

    @Test
    void unmatchedCallRoutesToRouteNotMatched() {
        ServiceResponseContract sentinel = ServiceResponse.unimplemented("nope");
        when(routeNotMatchedHandler.routeNotMatched(any(), any())).thenReturn(sentinel);

        ServiceResponseContract result = router.dispatch(call(new CancellationToken()));

        assertSame(sentinel, result);
        verify(routeMatchedHandler, never()).routeMatched(any(), any());
    }

    @Test
    void matchedCallRunsHandlerAndRouteDispatched() {
        Route route = route((c, r) -> ServiceResponse.ok("body"));
        collection.add(route);
        ServiceResponseContract finalResponse = ServiceResponse.ok("final");

        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenReturn(new RouteMatchedResult(route, null));
        when(routeDispatchedHandler.routeDispatched(any(), any(), any())).thenReturn(finalResponse);

        ServiceResponseContract result = router.dispatch(call(new CancellationToken()));

        assertSame(finalResponse, result);
        verify(routeMatchedHandler).add(new Class[0]);
    }

    @Test
    void routeMatchedShortCircuitSkipsHandler() {
        boolean[] handlerRan = {false};
        Route route =
                route(
                        (c, r) -> {
                            handlerRan[0] = true;
                            return ServiceResponse.ok();
                        });
        collection.add(route);
        ServiceResponseContract shortCircuit = ServiceResponse.of(io.valkyrja.grpc.message.status.Status.permissionDenied(null));

        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenReturn(new RouteMatchedResult(route, shortCircuit));

        ServiceResponseContract result = router.dispatch(call(new CancellationToken()));

        assertSame(shortCircuit, result);
        org.junit.jupiter.api.Assertions.assertFalse(handlerRan[0]);
        verify(routeDispatchedHandler, never()).routeDispatched(any(), any(), any());
    }

    @Test
    void preCheckCancellationSkipsRouteMatched() {
        Route route = route((c, r) -> ServiceResponse.ok());
        collection.add(route);
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);

        ServiceResponseContract result = router.dispatch(call(token));

        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
        verify(routeMatchedHandler, never()).routeMatched(any(), any());
    }

    @Test
    void postCheckCancellationSkipsRouteDispatched() {
        CancellationToken token = new CancellationToken();
        Route route =
                route(
                        (c, r) -> {
                            token.cancel(CancellationReason.DEADLINE_EXCEEDED);
                            return ServiceResponse.ok("body");
                        });
        collection.add(route);

        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenReturn(new RouteMatchedResult(route, null));

        ServiceResponseContract result = router.dispatch(call(token));

        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.getStatus().getCode());
        verify(routeDispatchedHandler, never()).routeDispatched(any(), any(), any());
    }
}
