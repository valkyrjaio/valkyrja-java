/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.grpc.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.CallReceivedHandler;
import io.valkyrja.grpc.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteMatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.grpc.middleware.handler.SendingResponseHandler;
import io.valkyrja.grpc.middleware.handler.TerminatedHandler;
import io.valkyrja.grpc.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.grpc.routing.collection.RouteCollection;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.dispatcher.Router;
import io.valkyrja.grpc.server.handler.ServiceHandler;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Proves per-route {@code SendingResponse} and {@code Terminated} middleware actually fire: the
 * {@code Router} registers them onto the stage handlers, and the {@code ServiceHandler} invokes the
 * same shared handler instances.
 */
final class PerRouteMiddlewareTest {

    static final String METHOD = "/pkg.Greeter/SayHello";

    public static final class RecordingSending implements SendingResponseMiddlewareContract {
        static boolean ran;

        @Override
        public ServiceResponseContract sendingResponse(
                ServiceCallContract call,
                ServiceResponseContract response,
                SendingResponseHandlerContract handler) {
            ran = true;
            return handler.sendingResponse(call, response);
        }
    }

    public static final class RecordingTerminated implements TerminatedMiddlewareContract {
        static boolean ran;

        @Override
        public void terminated(
                ServiceCallContract call,
                ServiceResponseContract response,
                TerminatedHandlerContract handler) {
            ran = true;
            handler.terminated(call, response);
        }
    }

    @Test
    void perRouteSendingAndTerminatedMiddlewareFire() {
        RecordingSending.ran = false;
        RecordingTerminated.ran = false;

        ContainerContract container = new Container();
        container.setSingleton(RecordingSending.class, new RecordingSending());
        container.setSingleton(RecordingTerminated.class, new RecordingTerminated());

        // Stage handlers shared between Router and ServiceHandler.
        CallReceivedHandler callReceivedHandler = new CallReceivedHandler(container);
        RouteMatchedHandler routeMatchedHandler = new RouteMatchedHandler(container);
        RouteNotMatchedHandler routeNotMatchedHandler = new RouteNotMatchedHandler(container);
        RouteDispatchedHandler routeDispatchedHandler = new RouteDispatchedHandler(container);
        ThrowableCaughtHandler throwableCaughtHandler = new ThrowableCaughtHandler(container);
        SendingResponseHandler sendingResponseHandler = new SendingResponseHandler(container);
        TerminatedHandler terminatedHandler = new TerminatedHandler(container);

        Router router =
                new Router(
                        container,
                        new RouteCollection()
                                .add(
                                        new Route(METHOD, (c, r) -> ServiceResponse.ok("hi"))
                                                .withSendingResponseMiddleware(
                                                        List.of(RecordingSending.class))
                                                .withTerminatedMiddleware(
                                                        List.of(RecordingTerminated.class))),
                        routeMatchedHandler,
                        routeNotMatchedHandler,
                        routeDispatchedHandler,
                        throwableCaughtHandler,
                        sendingResponseHandler,
                        terminatedHandler);

        ServiceHandler serviceHandler =
                new ServiceHandler(
                        container,
                        router,
                        callReceivedHandler,
                        throwableCaughtHandler,
                        sendingResponseHandler,
                        terminatedHandler,
                        false);

        ServiceCallContract call = ServiceCall.unary(METHOD, "req");

        // Adapter flow: run() (handle + sending), write to wire, then terminate.
        ServiceResponseContract response = serviceHandler.run(call);
        serviceHandler.terminate(call, response);

        assertTrue(RecordingSending.ran, "per-route SendingResponse middleware should fire");
        assertTrue(RecordingTerminated.ran, "per-route Terminated middleware should fire");
    }
}
