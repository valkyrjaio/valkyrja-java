/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.middleware.routematched;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.JsonResponse;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.server.middleware.routematched.ResponseStructMiddleware;
import io.valkyrja.tests.fixtures.http.struct.ResponseStructFixture;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ResponseStructMiddleware}. */
final class ResponseStructMiddlewareTest {

    private final ResponseStructMiddleware middleware = new ResponseStructMiddleware();
    private final ServerRequestContract request = mock(ServerRequestContract.class);
    private final RouteContract route = new Route("/x", "x", (container, r) -> new EmptyResponse());

    private RouteDispatchedHandlerContract passThroughHandler() {
        var handler = mock(RouteDispatchedHandlerContract.class);
        when(handler.routeDispatched(any(), any(), any())).thenAnswer(inv -> inv.getArgument(1));
        return handler;
    }

    @Test
    void restructuresJsonResponseWhenRouteHasResponseStruct() {
        var json =
                new JsonResponse(
                        Map.of("id", 7, "name", "bob"), StatusCode.OK, new HeaderCollection());
        var routeWithStruct = route.withResponseStruct(new ResponseStructFixture());

        var result =
                middleware.routeDispatched(request, json, routeWithStruct, passThroughHandler());

        assertTrue(result.getBody().toString().contains("identifier"));
        assertTrue(result.getBody().toString().contains("full_name"));
    }

    @Test
    void passesNonJsonResponseThrough() {
        var response = new EmptyResponse();
        var routeWithStruct = route.withResponseStruct(new ResponseStructFixture());

        var result =
                middleware.routeDispatched(
                        request, response, routeWithStruct, passThroughHandler());

        assertSame(response, result);
    }

    @Test
    void passesThroughWhenNoResponseStruct() {
        var json = new JsonResponse();

        var result = middleware.routeDispatched(request, json, route, passThroughHandler());

        assertSame(json, result);
    }
}
