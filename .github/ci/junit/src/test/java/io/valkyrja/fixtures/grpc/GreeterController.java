/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.attribute.Method;
import io.valkyrja.grpc.routing.attribute.Middleware;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import java.util.concurrent.atomic.AtomicInteger;

/** Fixture gRPC service controller exercising the {@code AttributeRouteCollector}. */
@Service(service = "pkg.Greeter")
public class GreeterController {

    @Method(name = "SayHello")
    public ServiceResponseContract sayHello(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("hello");
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

    /** Not annotated with {@link Method}; must be skipped by the collector. */
    public ServiceResponseContract notAnRpc(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok();
    }

    public static final class MatchedMiddleware implements RouteMatchedMiddlewareContract {
        @Override
        public RouteMatchedResult routeMatched(
                ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler) {
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
        @Override
        public ServiceResponseContract sendingResponse(
                ServiceCallContract call,
                ServiceResponseContract response,
                SendingResponseHandlerContract handler) {
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
