/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.attribute.Method;
import io.valkyrja.grpc.routing.attribute.Middleware;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Fixture gRPC service controller exercising the {@code AttributeRouteCollector}. */
@Service(service = "pkg.Greeter")
public class GreeterControllerFixture {

    @Method(name = "SayHello")
    public ServiceResponseContract sayHello(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("hello");
    }

    /** Unary (1 → 1): returns a single byte[] message, for the end-to-end unary verification. */
    @Method(name = "Ping")
    public ServiceResponseContract ping(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("pong".getBytes());
    }

    /** Server-streaming (1 → N): buffered path returns several byte[] messages for one request. */
    @Method(name = "Fanout", serverStreaming = true)
    public ServiceResponseContract fanout(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok()
                .withMessages(List.<Object>of("x".getBytes(), "y".getBytes(), "z".getBytes()));
    }

    @Method(name = "StreamHellos", clientStreaming = true, serverStreaming = true)
    @Middleware(name = MatchedMiddleware.class)
    @Middleware(name = DispatchedMiddleware.class)
    @Middleware(name = CaughtMiddleware.class)
    @Middleware(name = SendingMiddleware.class)
    @Middleware(name = ResponseSentMiddleware.class)
    public ServiceResponseContract streamHellos(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok();
    }

    /**
     * Bidirectional streaming echo: reads each inbound message from the live stream and pushes it
     * straight back through the outbound sink, then returns a terminal OK. Exercises the streaming
     * dispatch model.
     */
    @Method(name = "Echo", clientStreaming = true, serverStreaming = true)
    @Middleware(name = SendingMiddleware.class)
    @Middleware(name = ResponseSentMiddleware.class)
    public ServiceResponseContract echo(ContainerContract container, RouteContract route) {
        ServiceCallContract call = container.getSingleton(ServiceCallContract.class);
        for (Object message : call.getMessages()) {
            call.send(message);
        }
        return ServiceResponse.ok();
    }

    /** Client-streaming (not bidirectional): buffered like unary, so it takes the buffered path. */
    @Method(name = "Collect", clientStreaming = true)
    public ServiceResponseContract collect(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("collected".getBytes());
    }

    @Method(name = "Boom")
    public ServiceResponseContract boom(ContainerContract container, RouteContract route) {
        throw new IllegalStateException("handler failure");
    }

    /**
     * Throws the framework's own cancellation signal, as a handler calling {@code throwIfCancelled}
     * would. The collector must surface it unwrapped so it maps to {@code CANCELLED} rather than
     * {@code INTERNAL}.
     */
    @Method(name = "Cancelled")
    public ServiceResponseContract cancelled(ContainerContract container, RouteContract route) {
        throw new CancelledException("cancelled by test", CancellationReason.DEADLINE_EXCEEDED);
    }

    /**
     * Throws an {@link Error}; the collector must rethrow it unwrapped, not as a RuntimeException.
     */
    @Method(name = "ThrowsError")
    public ServiceResponseContract throwsError(ContainerContract container, RouteContract route) {
        throw new AssertionError("error from handler");
    }

    /**
     * Throws a checked exception (via a sneaky throw, since the handler signature declares none);
     * the collector wraps such a cause in a RuntimeException.
     */
    @Method(name = "Sneaky")
    public ServiceResponseContract sneaky(ContainerContract container, RouteContract route) {
        sneakyThrow(new java.io.IOException("checked from handler"));
        return ServiceResponse.ok();
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable throwable) throws E {
        throw (E) throwable;
    }

    /** Not annotated with {@link Method}; must be skipped by the collector. */
    public ServiceResponseContract notAnRpc(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok();
    }

    public static final class MatchedMiddleware implements RouteMatchedMiddlewareContract {
        @Override
        public RouteMatchedResult routeMatched(
                ServiceCallContract call,
                RouteContract route,
                RouteMatchedHandlerContract handler) {
            return handler.routeMatched(call, route);
        }
    }

    public static final class DispatchedMiddleware implements RouteDispatchedMiddlewareContract {
        @Override
        public ServiceResponseContract routeDispatched(
                ServiceCallContract call,
                ServiceResponseContract response,
                RouteContract route,
                RouteDispatchedHandlerContract handler) {
            return handler.routeDispatched(call, response, route);
        }
    }

    public static final class CaughtMiddleware implements ThrowableCaughtMiddlewareContract {
        @Override
        public ServiceResponseContract throwableCaught(
                ServiceCallContract call,
                ServiceResponseContract response,
                Throwable throwable,
                ThrowableCaughtHandlerContract handler) {
            return handler.throwableCaught(call, response, throwable);
        }
    }

    public static final class SendingMiddleware implements SendingResponseMiddlewareContract {

        /**
         * Counts sending-response invocations so tests can assert the stage ran. Reset per test.
         */
        public static final AtomicInteger calls = new AtomicInteger();

        @Override
        public ServiceResponseContract sendingResponse(
                ServiceCallContract call,
                ServiceResponseContract response,
                SendingResponseHandlerContract handler) {
            calls.incrementAndGet();
            return handler.sendingResponse(call, response);
        }
    }

    public static final class ResponseSentMiddleware implements ResponseSentMiddlewareContract {

        /** Counts terminations so tests can assert the stage ran. Reset per test. */
        public static final AtomicInteger calls = new AtomicInteger();

        @Override
        public void responseSent(
                ServiceCallContract call,
                ServiceResponseContract response,
                ResponseSentHandlerContract handler) {
            calls.incrementAndGet();
            handler.responseSent(call, response);
        }
    }
}
