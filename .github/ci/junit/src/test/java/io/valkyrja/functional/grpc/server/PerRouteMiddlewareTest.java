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
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.CallReceivedHandler;
import io.valkyrja.grpc.middleware.handler.ResponseSentHandler;
import io.valkyrja.grpc.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteMatchedHandler;
import io.valkyrja.grpc.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.grpc.middleware.handler.SendingResponseHandler;
import io.valkyrja.grpc.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.routing.collection.RouteCollection;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.dispatcher.Router;
import io.valkyrja.grpc.server.handler.ServiceHandler;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Proves per-route {@code SendingResponse} and {@code ResponseSent} middleware actually fire: the
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

    public static final class RecordingResponseSent implements ResponseSentMiddlewareContract {
        static boolean ran;

        @Override
        public void responseSent(
                ServiceCallContract call,
                ServiceResponseContract response,
                ResponseSentHandlerContract handler) {
            ran = true;
            handler.responseSent(call, response);
        }
    }

    @Test
    void perRouteSendingAndResponseSentMiddlewareFire() {
        RecordingSending.ran = false;
        RecordingResponseSent.ran = false;

        ContainerContract container = new Container();
        container.setSingleton(RecordingSending.class, new RecordingSending());
        container.setSingleton(RecordingResponseSent.class, new RecordingResponseSent());

        // Stage handlers shared between Router and ServiceHandler.
        CallReceivedHandler callReceivedHandler = new CallReceivedHandler(container);
        RouteMatchedHandler routeMatchedHandler = new RouteMatchedHandler(container);
        RouteNotMatchedHandler routeNotMatchedHandler = new RouteNotMatchedHandler(container);
        RouteDispatchedHandler routeDispatchedHandler = new RouteDispatchedHandler(container);
        ThrowableCaughtHandler throwableCaughtHandler = new ThrowableCaughtHandler(container);
        SendingResponseHandler sendingResponseHandler = new SendingResponseHandler(container);
        ResponseSentHandler responseSentHandler = new ResponseSentHandler(container);

        Router router =
                new Router(
                        container,
                        new RouteCollection()
                                .add(
                                        new Route(METHOD, (c, r) -> ServiceResponse.ok("hi"))
                                                .withSendingResponseMiddleware(
                                                        List.of(RecordingSending.class))
                                                .withResponseSentMiddleware(
                                                        List.of(RecordingResponseSent.class))),
                        routeMatchedHandler,
                        routeNotMatchedHandler,
                        routeDispatchedHandler,
                        throwableCaughtHandler,
                        sendingResponseHandler,
                        responseSentHandler);

        ServiceHandler serviceHandler =
                new ServiceHandler(
                        container,
                        router,
                        callReceivedHandler,
                        throwableCaughtHandler,
                        sendingResponseHandler,
                        responseSentHandler,
                        false);

        ServiceCallContract call = ServiceCall.unary(METHOD, "req");

        // Adapter flow: run() (handle + sending), write to wire, then terminate.
        ServiceResponseContract response = serviceHandler.run(call);
        serviceHandler.terminate(call, response);

        assertTrue(RecordingSending.ran, "per-route SendingResponse middleware should fire");
        assertTrue(RecordingResponseSent.ran, "per-route ResponseSent middleware should fire");
    }
}
