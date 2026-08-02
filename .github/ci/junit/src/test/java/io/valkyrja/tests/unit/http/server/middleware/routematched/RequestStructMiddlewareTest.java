/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.middleware.routematched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.server.middleware.routematched.RequestStructMiddleware;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestStructMiddleware}. */
final class RequestStructMiddlewareTest {

    private final RequestStructMiddleware middleware = new RequestStructMiddleware();
    private final ServerRequestContract request = mock(ServerRequestContract.class);
    private final RouteContract baseRoute =
            new Route("/x", "x", (container, route) -> new EmptyResponse());

    private RouteMatchedHandlerContract passThroughHandler() {
        var handler = mock(RouteMatchedHandlerContract.class);
        when(handler.routeMatched(any(), any()))
                .thenAnswer(inv -> new RouteMatchedResult(inv.getArgument(1), null));
        return handler;
    }

    @Test
    void passesThroughWhenNoStruct() {
        var result = middleware.routeMatched(request, baseRoute, passThroughHandler());

        assertNull(result.response());
    }

    @Test
    void rejectsExtraDataWithPayloadTooLarge() {
        var struct = mock(RequestStructContract.class);
        when(struct.determineIfRequestContainsExtraData(any())).thenReturn(true);
        var route = baseRoute.withRequestStruct(struct);

        var result = middleware.routeMatched(request, route, passThroughHandler());

        assertEquals(StatusCode.PAYLOAD_TOO_LARGE, result.response().getStatusCode());
    }

    @Test
    void rejectsInvalidRequestWithBadRequest() {
        var struct = mock(RequestStructContract.class);
        when(struct.determineIfRequestContainsExtraData(any())).thenReturn(false);
        when(struct.validate(any())).thenReturn(() -> false);
        var route = baseRoute.withRequestStruct(struct);

        var result = middleware.routeMatched(request, route, passThroughHandler());

        assertEquals(StatusCode.BAD_REQUEST, result.response().getStatusCode());
    }

    @Test
    void passesThroughWhenValid() {
        var struct = mock(RequestStructContract.class);
        when(struct.determineIfRequestContainsExtraData(any())).thenReturn(false);
        when(struct.validate(any())).thenReturn(() -> true);
        var route = baseRoute.withRequestStruct(struct);

        var result = middleware.routeMatched(request, route, passThroughHandler());

        assertNull(result.response());
    }
}
