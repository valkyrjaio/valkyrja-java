/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoRequestStructException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoResponseStructException;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link Route}. */
final class RouteTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private static Route route() {
        return new Route("/users", "users.index", HANDLER);
    }

    @Test
    void defaultsToHeadAndGetMethods() {
        var route = route();

        assertEquals("/users", route.getPath());
        assertEquals("users.index", route.getName());
        assertSame(HANDLER, route.getHandler());
        assertTrue(route.hasRequestMethod(RequestMethod.GET));
        assertTrue(route.hasRequestMethod(RequestMethod.HEAD));
        assertFalse(route.hasRequestMethod(RequestMethod.POST));
    }

    @Test
    void pathFilteringTrimsSlashes() {
        assertEquals("/users", route().withPath("//users//").getPath());
        assertEquals("/", route().withPath("").getPath());
        assertEquals("/users/list", route().withAddedPath("list").getPath());
    }

    @Test
    void nameAndHandlerMutations() {
        var route = route();

        assertEquals("renamed", route.withName("renamed").getName());
        assertEquals("users.indexsub", route.withAddedName("sub").getName());
        BiFunction<ContainerContract, RouteContract, ResponseContract> other =
                (c, r) -> new EmptyResponse();
        assertSame(other, route.withHandler(other).getHandler());
    }

    @Test
    void requestMethodMutations() {
        var route =
                route().withRequestMethods(RequestMethod.POST)
                        .withAddedRequestMethods(RequestMethod.PUT);

        assertTrue(route.hasRequestMethod(RequestMethod.POST));
        assertTrue(route.hasRequestMethod(RequestMethod.PUT));
        assertEquals(2, route.getRequestMethods().size());
    }

    @Test
    void middlewareCollectionsAreManaged() {
        var route = route();

        assertTrue(
                route.withRouteMatchedMiddleware()
                        .withAddedRouteMatchedMiddleware()
                        .getRouteMatchedMiddleware()
                        .isEmpty());
        assertTrue(
                route.withRouteDispatchedMiddleware()
                        .withAddedRouteDispatchedMiddleware()
                        .getRouteDispatchedMiddleware()
                        .isEmpty());
        assertTrue(
                route.withThrowableCaughtMiddleware()
                        .withAddedThrowableCaughtMiddleware()
                        .getThrowableCaughtMiddleware()
                        .isEmpty());
        assertTrue(
                route.withSendingResponseMiddleware()
                        .withAddedSendingResponseMiddleware()
                        .getSendingResponseMiddleware()
                        .isEmpty());
        assertTrue(
                route.withResponseSentMiddleware()
                        .withAddedResponseSentMiddleware()
                        .getResponseSentMiddleware()
                        .isEmpty());
    }

    @Test
    void requestStruct() {
        var route = route();
        var struct = mock(RequestStructContract.class);

        assertFalse(route.hasRequestStruct());
        assertThrows(HttpRoutingNoRequestStructException.class, route::getRequestStruct);
        assertSame(struct, route.withRequestStruct(struct).getRequestStruct());
    }

    @Test
    void responseStruct() {
        var route = route();
        var struct = mock(ResponseStructContract.class);

        assertFalse(route.hasResponseStruct());
        assertThrows(HttpRoutingNoResponseStructException.class, route::getResponseStruct);
        assertSame(struct, route.withResponseStruct(struct).getResponseStruct());
    }

    @Test
    void withAddedRequestMethodsSkipsExistingMethods() {
        var updated = route().withAddedRequestMethods(RequestMethod.GET, RequestMethod.POST);

        assertTrue(updated.hasRequestMethod(RequestMethod.GET));
        assertTrue(updated.hasRequestMethod(RequestMethod.POST));
    }
}
