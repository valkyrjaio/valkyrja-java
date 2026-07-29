/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.grpc.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.SendingResponseHandler;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link SendingResponseHandler} — an always-run stage with no cancellation short-circuit.
 */
final class SendingResponseHandlerTest {

    static final ServiceResponseContract REPLACEMENT = ServiceResponse.ok("sent");

    static boolean ran;

    static final class Recording implements SendingResponseMiddlewareContract {
        @Override
        public ServiceResponseContract sendingResponse(
                ServiceCallContract call,
                ServiceResponseContract response,
                SendingResponseHandlerContract handler) {
            ran = true;
            return handler.sendingResponse(call, response);
        }
    }

    static final class Replace implements SendingResponseMiddlewareContract {
        @Override
        public ServiceResponseContract sendingResponse(
                ServiceCallContract call,
                ServiceResponseContract response,
                SendingResponseHandlerContract handler) {
            ran = true;
            return REPLACEMENT;
        }
    }

    private ServiceCallContract call(CancellationToken token) {
        return new ServiceCall(
                "/pkg.A/M",
                new Metadata(),
                Deadline.none(),
                token,
                Peer.insecure("x"),
                List.of(),
                null);
    }

    @SuppressWarnings("unchecked")
    private ContainerContract containerWith(Object instance) {
        ContainerContract container = new Container();
        container.setSingleton((Class<Object>) instance.getClass(), instance);
        return container;
    }

    @Test
    void emptyChainReturnsResponseUnchanged() {
        SendingResponseHandler handler = new SendingResponseHandler(new Container());
        ServiceResponseContract response = ServiceResponse.ok();
        assertSame(response, handler.sendingResponse(call(new CancellationToken()), response));
    }

    @Test
    void passThroughReachesTerminal() {
        ran = false;
        SendingResponseHandler handler =
                new SendingResponseHandler(containerWith(new Recording()), Recording.class);
        ServiceResponseContract response = ServiceResponse.ok();
        assertSame(response, handler.sendingResponse(call(new CancellationToken()), response));
        assertTrue(ran);
    }

    @Test
    void middlewareCanReplaceResponse() {
        ran = false;
        SendingResponseHandler handler =
                new SendingResponseHandler(containerWith(new Replace()), Replace.class);
        assertSame(
                REPLACEMENT,
                handler.sendingResponse(call(new CancellationToken()), ServiceResponse.ok()));
    }

    @Test
    void runsEvenWhenCallIsCancelled() {
        ran = false;
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        SendingResponseHandler handler =
                new SendingResponseHandler(containerWith(new Recording()), Recording.class);
        handler.sendingResponse(call(token), ServiceResponse.ok());
        assertTrue(ran);
    }
}
