/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.routing.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.collector.AttributeRouteCollector;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import io.valkyrja.fixtures.grpc.GreeterController;
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
        Map<String, RouteContract> routes = collect(GreeterController.class);
        assertTrue(routes.containsKey("/pkg.Greeter/SayHello"));
        assertTrue(routes.containsKey("/pkg.Greeter/StreamHellos"));
        assertTrue(routes.containsKey("/pkg.Greeter/Boom"));
    }

    @Test
    void skipsMethodsWithoutGrpcMethod() {
        Map<String, RouteContract> routes = collect(GreeterController.class);
        // Four annotated methods; notAnRpc() is excluded.
        assertEquals(4, routes.size());
    }

    @Test
    void parsesServiceAndMethodName() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/SayHello");
        assertEquals("pkg.Greeter", route.getService());
        assertEquals("SayHello", route.getMethodName());
    }

    @Test
    void carriesStreamingFlags() {
        Map<String, RouteContract> routes = collect(GreeterController.class);
        RouteContract unary = routes.get("/pkg.Greeter/SayHello");
        RouteContract streaming = routes.get("/pkg.Greeter/StreamHellos");
        assertFalse(unary.isClientStreaming());
        assertFalse(unary.isServerStreaming());
        assertTrue(streaming.isClientStreaming());
        assertTrue(streaming.isServerStreaming());
    }

    @Test
    void dispatchesEachMiddlewareToItsStage() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/StreamHellos");
        assertEquals(
                List.of(GreeterController.MatchedMiddleware.class), route.getRouteMatchedMiddleware());
        assertEquals(
                List.of(GreeterController.DispatchedMiddleware.class),
                route.getRouteDispatchedMiddleware());
        assertEquals(
                List.of(GreeterController.CaughtMiddleware.class),
                route.getThrowableCaughtMiddleware());
        assertEquals(
                List.of(GreeterController.SendingMiddleware.class),
                route.getSendingResponseMiddleware());
        assertEquals(
                List.of(GreeterController.ResponseSentMiddleware.class),
                route.getResponseSentMiddleware());
    }

    @Test
    void unaryRouteHasNoMiddleware() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/SayHello");
        assertTrue(route.getRouteMatchedMiddleware().isEmpty());
        assertTrue(route.getResponseSentMiddleware().isEmpty());
    }

    @Test
    void reflectiveHandlerInvokesTheAnnotatedMethod() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/SayHello");
        ServiceResponseContract response = route.getHandler().apply(new Container(), route);
        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void reflectiveHandlerWrapsThrownExceptions() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/Boom");
        Container container = new Container();
        assertThrows(RuntimeException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void reflectiveHandlerPropagatesTheHandlersOwnThrowable() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/Boom");
        Container container = new Container();
        // The handler's IllegalStateException must not be buried under a reflection wrapper.
        assertThrows(
                IllegalStateException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void reflectiveHandlerPropagatesCancelledExceptionUnwrapped() {
        RouteContract route = collect(GreeterController.class).get("/pkg.Greeter/Cancelled");
        Container container = new Container();
        // Regression: rewrapping in RuntimeException made ServiceHandler report INTERNAL instead
        // of CANCELLED, silently defeating cooperative cancellation.
        CancelledException thrown =
                assertThrows(
                        CancelledException.class,
                        () -> route.getHandler().apply(container, route));
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, thrown.getReason());
    }

    @Test
    void ignoresClassesWithoutGrpcServiceAnnotation() {
        assertTrue(collect(String.class).isEmpty());
    }

    @Test
    void collectsAcrossMultipleClasses() {
        Map<String, RouteContract> routes = collect(GreeterController.class, String.class);
        assertEquals(4, routes.size());
    }
}
