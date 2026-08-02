/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.routing.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.collector.AttributeRouteCollector;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import io.valkyrja.tests.fixtures.grpc.GreeterControllerFixture;
import io.valkyrja.tests.fixtures.grpc.NoDefaultConstructorControllerFixture;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Test the {@link AttributeRouteCollector}. */
final class AttributeRouteCollectorTest {

    private Map<String, RouteContract> collect(Class<?>... classes) {
        return new AttributeRouteCollector()
                .getRoutes(classes).stream()
                        .collect(Collectors.toMap(RouteContract::getMethod, Function.identity()));
    }

    @Test
    void buildsRoutesKeyedByFullyQualifiedMethod() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class);
        assertTrue(routes.containsKey("/pkg.Greeter/SayHello"));
        assertTrue(routes.containsKey("/pkg.Greeter/StreamHellos"));
        assertTrue(routes.containsKey("/pkg.Greeter/Boom"));
    }

    @Test
    void skipsMethodsWithoutGrpcMethod() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class);
        // Six annotated methods; notAnRpc() is excluded.
        assertEquals(10, routes.size());
    }

    @Test
    void parsesServiceAndMethodName() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        assertEquals("pkg.Greeter", route.getService());
        assertEquals("SayHello", route.getMethodName());
    }

    @Test
    void carriesStreamingFlags() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class);
        RouteContract unary = routes.get("/pkg.Greeter/SayHello");
        RouteContract streaming = routes.get("/pkg.Greeter/StreamHellos");
        assertFalse(unary.isClientStreaming());
        assertFalse(unary.isServerStreaming());
        assertTrue(streaming.isClientStreaming());
        assertTrue(streaming.isServerStreaming());
    }

    @Test
    void dispatchesEachMiddlewareToItsStage() {
        RouteContract route =
                collect(GreeterControllerFixture.class).get("/pkg.Greeter/StreamHellos");
        assertEquals(
                List.of(GreeterControllerFixture.MatchedMiddleware.class),
                route.getRouteMatchedMiddleware());
        assertEquals(
                List.of(GreeterControllerFixture.DispatchedMiddleware.class),
                route.getRouteDispatchedMiddleware());
        assertEquals(
                List.of(GreeterControllerFixture.CaughtMiddleware.class),
                route.getThrowableCaughtMiddleware());
        assertEquals(
                List.of(GreeterControllerFixture.SendingMiddleware.class),
                route.getSendingResponseMiddleware());
        assertEquals(
                List.of(GreeterControllerFixture.ResponseSentMiddleware.class),
                route.getResponseSentMiddleware());
    }

    @Test
    void unaryRouteHasNoMiddleware() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        assertTrue(route.getRouteMatchedMiddleware().isEmpty());
        assertTrue(route.getResponseSentMiddleware().isEmpty());
    }

    @Test
    void reflectiveHandlerInvokesTheAnnotatedMethod() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        ServiceResponseContract response = route.getHandler().apply(new Container(), route);
        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void reflectiveHandlerWrapsThrownExceptions() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Boom");
        Container container = new Container();
        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void reflectiveHandlerPropagatesTheHandlersOwnThrowable() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Boom");
        Container container = new Container();
        // The handler's IllegalStateException must not be buried under a reflection wrapper.
        assertThrows(IllegalStateException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void reflectiveHandlerPropagatesCancelledExceptionUnwrapped() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Cancelled");
        Container container = new Container();
        // Regression: rewrapping in RuntimeException made ServiceHandler report INTERNAL instead
        // of CANCELLED, silently defeating cooperative cancellation.
        CancelledException thrown =
                assertThrows(
                        CancelledException.class, () -> route.getHandler().apply(container, route));
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, thrown.getReason());
    }

    @Test
    void ignoresClassesWithoutGrpcServiceAnnotation() {
        assertTrue(collect(String.class).isEmpty());
    }

    @Test
    void collectsAcrossMultipleClasses() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class, String.class);
        assertEquals(10, routes.size());
    }

    @Test
    void reflectiveHandlerRethrowsErrorsUnwrapped() {
        RouteContract route =
                collect(GreeterControllerFixture.class).get("/pkg.Greeter/ThrowsError");
        Container container = new Container();
        assertThrows(AssertionError.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void reflectiveHandlerWrapsCheckedCauses() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Sneaky");
        Container container = new Container();
        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class, () -> route.getHandler().apply(container, route));
        assertInstanceOf(java.io.IOException.class, thrown.getCause());
    }

    @Test
    void reflectiveHandlerWrapsInstantiationFailure() {
        RouteContract route =
                collect(NoDefaultConstructorControllerFixture.class).get("/pkg.NoCtor/Ping");
        Container container = new Container();
        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }
}
