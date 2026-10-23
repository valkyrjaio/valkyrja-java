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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.collector.AttributeRouteCollector;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidHandlerException;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import io.valkyrja.tests.fixtures.grpc.AbsentHandlerControllerFixture;
import io.valkyrja.tests.fixtures.grpc.GreeterControllerFixture;
import io.valkyrja.tests.fixtures.grpc.HandlerControllerFixture;
import io.valkyrja.tests.fixtures.grpc.HiddenControllerAccessFixture;
import io.valkyrja.tests.fixtures.grpc.InaccessibleHandlerControllerFixture;
import io.valkyrja.tests.fixtures.grpc.NoDefaultConstructorControllerFixture;
import io.valkyrja.tests.fixtures.grpc.NonStaticHandlerControllerFixture;
import io.valkyrja.tests.fixtures.grpc.VoidReturnControllerFixture;
import io.valkyrja.tests.fixtures.grpc.WrongParametersControllerFixture;
import io.valkyrja.tests.fixtures.grpc.WrongReturnHandlerControllerFixture;
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

    /**
     * A container holding the greeter controller, which the collector resolves its handler from.
     */
    private Container container() {
        Container container = new Container();
        container.setSingleton(GreeterControllerFixture.class, new GreeterControllerFixture());

        return container;
    }

    private ServiceResponseContract dispatch(String method) {
        RouteContract route = collect(HandlerControllerFixture.class).get(method);

        return route.getHandler().apply(new Container(), route);
    }

    @Test
    void buildsRoutesKeyedByFullyQualifiedMethod() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class);
        assertTrue(routes.containsKey("/pkg.Greeter/SayHello"));
        assertTrue(routes.containsKey("/pkg.Greeter/StreamHellos"));
        assertTrue(routes.containsKey("/pkg.Greeter/Boom"));
    }

    @Test
    void skipsMethodsWithoutARouteAttribute() {
        Map<String, RouteContract> routes = collect(GreeterControllerFixture.class);
        // Ten annotated methods; notAnRpc() is excluded.
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
    void routeNamesNoMessageTypesByDefault() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        assertNull(route.getRequestType());
        assertNull(route.getResponseType());
    }

    @Test
    void carriesTheMessageTypesTheRouteNames() {
        RouteContract route = collect(HandlerControllerFixture.class).get("/pkg.Handler/Typed");
        assertEquals(String.class, route.getRequestType());
        assertEquals(Integer.class, route.getResponseType());
    }

    @Test
    void carriesTheMiddlewareTheRouteAttributeNames() {
        RouteContract route = collect(HandlerControllerFixture.class).get("/pkg.Handler/Typed");
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
    void theMiddlewareAttributeAddsToTheListTheRouteAttributeNames() {
        RouteContract route = collect(HandlerControllerFixture.class).get("/pkg.Handler/Typed");
        // The route attribute names the matched stage once, and the convenience attribute names it
        // again, so the stage holds both — a list field adds the convenience attribute's value.
        assertEquals(
                List.of(
                        GreeterControllerFixture.MatchedMiddleware.class,
                        GreeterControllerFixture.MatchedMiddleware.class),
                route.getRouteMatchedMiddleware());
    }

    @Test
    void handlerResolvesTheControllerFromTheContainer() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        ServiceResponseContract response = route.getHandler().apply(container(), route);
        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void handlerReachesAControllerThatTakesAConstructorDependency() {
        RouteContract route =
                collect(NoDefaultConstructorControllerFixture.class).get("/pkg.NoCtor/Ping");
        Container container = new Container();
        container.setSingleton(
                NoDefaultConstructorControllerFixture.class,
                new NoDefaultConstructorControllerFixture("injected"));

        // Regression: the collector built the controller with its no-argument constructor, so a
        // controller with a dependency could not be reached at all.
        ServiceResponseContract response = route.getHandler().apply(container, route);
        assertEquals("injected", response.getMessages().iterator().next());
    }

    @Test
    void handlerReportsAnUnboundController() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/SayHello");
        Container container = new Container();
        var handler = route.getHandler();
        assertThrows(
                ContainerInvalidReferenceException.class, () -> handler.apply(container, route));
    }

    @Test
    void handlerPropagatesTheHandlersOwnThrowable() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Boom");
        Container container = container();
        // The handler's IllegalStateException must not be buried under a reflection wrapper.
        assertThrows(IllegalStateException.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void handlerPropagatesCancelledExceptionUnwrapped() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Cancelled");
        Container container = container();
        // Regression: rewrapping in RuntimeException made ServiceHandler report INTERNAL instead
        // of CANCELLED, silently defeating cooperative cancellation.
        CancelledException thrown =
                assertThrows(
                        CancelledException.class, () -> route.getHandler().apply(container, route));
        assertEquals(CancellationReason.DEADLINE_EXCEEDED, thrown.getReason());
    }

    @Test
    void handlerRethrowsErrorsUnwrapped() {
        RouteContract route =
                collect(GreeterControllerFixture.class).get("/pkg.Greeter/ThrowsError");
        Container container = container();
        assertThrows(AssertionError.class, () -> route.getHandler().apply(container, route));
    }

    @Test
    void handlerWrapsCheckedCauses() {
        RouteContract route = collect(GreeterControllerFixture.class).get("/pkg.Greeter/Sneaky");
        Container container = container();
        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class, () -> route.getHandler().apply(container, route));
        assertInstanceOf(java.io.IOException.class, thrown.getCause());
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
    void runsTheHandlerTheRouteMemberNames() {
        assertEquals("element", dispatch("/pkg.Handler/Element").getMessages().iterator().next());
    }

    @Test
    void runsTheHandlerTheConvenienceAttributeNames() {
        assertEquals(
                "annotation", dispatch("/pkg.Handler/Annotation").getMessages().iterator().next());
    }

    @Test
    void theConvenienceAttributeWinsOverTheRouteMember() {
        // The member names a handler that does not exist, so a dispatch that reads it would throw.
        assertEquals(
                "annotation", dispatch("/pkg.Handler/Precedence").getMessages().iterator().next());
    }

    @Test
    void reportsAHandlerMethodThatDoesNotExistWhenItCollects() {
        // The collection runs once at bootstrap, so a mis-named handler fails there rather than
        // on the first call that reaches the route.
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(AbsentHandlerControllerFixture.class));
        assertTrue(thrown.getMessage().contains("absentHandler"));
        assertInstanceOf(NoSuchMethodException.class, thrown.getCause());
    }

    @Test
    void reportsAHandlerThatIsNotStatic() {
        // A named handler is invoked with no instance, so a non-static one would reach the call
        // only to throw a NullPointerException that names neither the class nor the method.
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(NonStaticHandlerControllerFixture.class));
        assertTrue(thrown.getMessage().contains("instanceHandler"));
        assertTrue(thrown.getMessage().contains("not static"));
    }

    @Test
    void reportsAHandlerThatReturnsNoResponse() {
        // Reflection matches a method on its parameters alone, so an unchecked return type
        // would reach the call as a ClassCastException.
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(WrongReturnHandlerControllerFixture.class));
        assertTrue(thrown.getMessage().contains("wrongReturnHandler"));
        assertTrue(
                thrown.getMessage()
                        .contains("java.lang.String rather than a ServiceResponseContract"));
    }

    @Test
    void aConvenienceAttributeNamingTheAbsentTypeNamesNoHandler() {
        RouteContract route =
                collect(HandlerControllerFixture.class).get("/pkg.Handler/VoidHandler");
        Container container = new Container();
        container.setSingleton(HandlerControllerFixture.class, new HandlerControllerFixture());

        // Both spellings mean the same thing, so the annotated method runs.
        assertEquals(
                "method",
                route.getHandler().apply(container, route).getMessages().iterator().next());
    }

    @Test
    void reportsANamedHandlerClassItCannotReadWhenItCollects() {
        // A route provider is under no rule to be public, so a handler on a package-private class
        // is a plausible mistake, and the invocation reads the declaring class.
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(InaccessibleHandlerControllerFixture.class));
        assertTrue(thrown.getMessage().contains("not accessible"));
    }

    @Test
    void reportsAControllerItCannotReadOnTheCall() {
        // A controller is resolved per call, so the collection cannot read its accessibility and
        // the invocation reports it instead.
        RouteContract route =
                collect(HiddenControllerAccessFixture.controllerClass()).get("/pkg.Hidden/Hidden");
        Container container = new Container();
        HiddenControllerAccessFixture.bind(container);

        var handler = route.getHandler();
        RuntimeException thrown =
                assertThrows(RuntimeException.class, () -> handler.apply(container, route));
        assertInstanceOf(IllegalAccessException.class, thrown.getCause());
    }

    @Test
    void reportsAControllerMethodThatReturnsNoResponse() {
        // The cached route calls the controller method directly, where a void method cannot
        // compile, so the collected route must reject it rather than hand the router a null.
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(VoidReturnControllerFixture.class));
        assertTrue(thrown.getMessage().contains("voidReturn"));
        assertTrue(thrown.getMessage().contains("void rather than a ServiceResponseContract"));
    }

    @Test
    void reportsAControllerMethodThatTakesTheWrongParameters() {
        GrpcRoutingInvalidHandlerException thrown =
                assertThrows(
                        GrpcRoutingInvalidHandlerException.class,
                        () -> collect(WrongParametersControllerFixture.class));
        assertTrue(thrown.getMessage().contains("wrongParameters"));
        assertTrue(
                thrown.getMessage()
                        .contains("does not take a ContainerContract and a RouteContract"));
    }
}
