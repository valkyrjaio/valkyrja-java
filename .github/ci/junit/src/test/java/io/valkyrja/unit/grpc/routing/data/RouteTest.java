/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the {@link Route} value type. */
final class RouteTest {

    private static final String METHOD = "/pkg.Greeter/SayHello";

    private static final BiFunction<ContainerContract, RouteContract, ServiceResponseContract>
            HANDLER = (container, route) -> ServiceResponse.ok();

    private static Route route() {
        return new Route(METHOD, HANDLER);
    }

    private interface Matched extends RouteMatchedMiddlewareContract {}

    private interface Dispatched extends RouteDispatchedMiddlewareContract {}

    private interface Caught extends ThrowableCaughtMiddlewareContract {}

    private interface Sending extends SendingResponseMiddlewareContract {}

    private interface ResponseSent extends ResponseSentMiddlewareContract {}

    @Test
    void parsesServiceAndMethodName() {
        Route route = route();
        assertEquals(METHOD, route.getMethod());
        assertEquals("pkg.Greeter", route.getService());
        assertEquals("SayHello", route.getMethodName());
    }

    @Test
    void rejectsMethodWithoutLeadingSlash() {
        assertThrows(
                GrpcRoutingInvalidMethodException.class,
                () -> new Route("pkg.Greeter/SayHello", HANDLER));
    }

    @Test
    void rejectsMethodWithSingleSlash() {
        assertThrows(
                GrpcRoutingInvalidMethodException.class, () -> new Route("/pkgGreeter", HANDLER));
    }

    @Test
    void rejectsMethodWithTrailingSlash() {
        assertThrows(
                GrpcRoutingInvalidMethodException.class, () -> new Route("/pkg.Greeter/", HANDLER));
    }

    @Test
    void defaultsAreNonStreamingWithNoTypes() {
        Route route = route();
        assertFalse(route.isClientStreaming());
        assertFalse(route.isServerStreaming());
        assertNull(route.getRequestType());
        assertNull(route.getResponseType());
        assertSame(HANDLER, route.getHandler());
    }

    @Test
    void withStreamingFlags() {
        Route route = route();
        assertTrue(route.withClientStreaming(true).isClientStreaming());
        assertTrue(route.withServerStreaming(true).isServerStreaming());
        assertFalse(route.isClientStreaming());
        assertFalse(route.isServerStreaming());
    }

    @Test
    void withRequestAndResponseType() {
        RouteContract route = route().withRequestType(String.class).withResponseType(Integer.class);
        assertEquals(String.class, route.getRequestType());
        assertEquals(Integer.class, route.getResponseType());
    }

    @Test
    void withHandlerReplaces() {
        BiFunction<ContainerContract, RouteContract, ServiceResponseContract> other =
                (container, r) -> ServiceResponse.unimplemented();
        RouteContract updated = route().withHandler(other);
        assertSame(other, updated.getHandler());
        assertSame(HANDLER, route().getHandler());
    }

    @Test
    void routeMatchedMiddleware() {
        RouteContract route = route().withRouteMatchedMiddleware(List.of(Matched.class));
        assertEquals(List.of(Matched.class), route.getRouteMatchedMiddleware());
        RouteContract added = route.withAddedRouteMatchedMiddleware(List.of(Matched.class));
        assertEquals(2, added.getRouteMatchedMiddleware().size());
    }

    @Test
    void routeDispatchedMiddleware() {
        RouteContract route = route().withRouteDispatchedMiddleware(List.of(Dispatched.class));
        assertEquals(List.of(Dispatched.class), route.getRouteDispatchedMiddleware());
        assertEquals(
                2,
                route.withAddedRouteDispatchedMiddleware(List.of(Dispatched.class))
                        .getRouteDispatchedMiddleware()
                        .size());
    }

    @Test
    void throwableCaughtMiddleware() {
        RouteContract route = route().withThrowableCaughtMiddleware(List.of(Caught.class));
        assertEquals(List.of(Caught.class), route.getThrowableCaughtMiddleware());
        assertEquals(
                2,
                route.withAddedThrowableCaughtMiddleware(List.of(Caught.class))
                        .getThrowableCaughtMiddleware()
                        .size());
    }

    @Test
    void sendingResponseMiddleware() {
        RouteContract route = route().withSendingResponseMiddleware(List.of(Sending.class));
        assertEquals(List.of(Sending.class), route.getSendingResponseMiddleware());
        assertEquals(
                2,
                route.withAddedSendingResponseMiddleware(List.of(Sending.class))
                        .getSendingResponseMiddleware()
                        .size());
    }

    @Test
    void responseSentMiddleware() {
        RouteContract route = route().withResponseSentMiddleware(List.of(ResponseSent.class));
        assertEquals(List.of(ResponseSent.class), route.getResponseSentMiddleware());
        assertEquals(
                2,
                route.withAddedResponseSentMiddleware(List.of(ResponseSent.class))
                        .getResponseSentMiddleware()
                        .size());
    }

    @Test
    void withMethodsAreImmutable() {
        Route base = route();
        base.withClientStreaming(true);
        base.withServerStreaming(true);
        base.withRequestType(String.class);
        base.withResponseType(String.class);
        base.withRouteMatchedMiddleware(List.of(Matched.class));
        assertFalse(base.isClientStreaming());
        assertTrue(base.getRouteMatchedMiddleware().isEmpty());
    }

    @Test
    void fullConstructorUsesExplicitServiceAndMethodName() {
        Route route =
                new Route(
                        METHOD,
                        "custom.Service",
                        "CustomMethod",
                        HANDLER,
                        String.class,
                        Integer.class,
                        true,
                        true,
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of());
        assertEquals("custom.Service", route.getService());
        assertEquals("CustomMethod", route.getMethodName());
        assertTrue(route.isClientStreaming());
        assertTrue(route.isServerStreaming());
    }

    @Test
    void middlewareListsAreImmutable() {
        // A "immutable" Route must not hand out a list callers can mutate underneath it.
        Route route = new Route("/pkg.Svc/M", (container, r) -> ServiceResponse.ok());
        RouteContract withMiddleware =
                route.withAddedRouteMatchedMiddleware(List.of(Matched.class));

        assertThrows(
                UnsupportedOperationException.class,
                () -> route.getRouteMatchedMiddleware().add(Matched.class));
        assertThrows(
                UnsupportedOperationException.class,
                () -> withMiddleware.getRouteMatchedMiddleware().add(Matched.class));
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        route.withRouteMatchedMiddleware(List.of(Matched.class))
                                .getRouteMatchedMiddleware()
                                .add(Matched.class));
    }
}
