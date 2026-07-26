/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.server.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.dispatcher.contract.RouterContract;
import io.valkyrja.grpc.server.handler.ServiceHandler;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Test the {@link ServiceHandler} orchestration and throwable mapping. */
@ExtendWith(MockitoExtension.class)
final class ServiceHandlerTest {

    @Mock private RouterContract router;
    @Mock private CallReceivedHandlerContract callReceivedHandler;
    @Mock private ThrowableCaughtHandlerContract throwableCaughtHandler;
    @Mock private SendingResponseHandlerContract sendingResponseHandler;
    @Mock private ResponseSentHandlerContract responseSentHandler;

    private ContainerContract container;

    @BeforeEach
    void setUp() {
        container = new Container();
    }

    private ServiceHandler handler(boolean debug) {
        return new ServiceHandler(
                container,
                router,
                callReceivedHandler,
                throwableCaughtHandler,
                sendingResponseHandler,
                responseSentHandler,
                debug);
    }

    private ServiceCallContract call(CancellationToken token) {
        return new ServiceCall(
                "/pkg.A/M", new Metadata(), Deadline.none(), token, Peer.insecure("x"), List.of(), null);
    }

    @Test
    void handleDispatchesThroughCallReceivedAndRouter() {
        ServiceCallContract call = call(new CancellationToken());
        ServiceResponseContract routed = ServiceResponse.ok("routed");
        when(callReceivedHandler.callReceived(any())).thenReturn(new CallReceivedResult(call, null));
        when(router.dispatch(any())).thenReturn(routed);

        assertSame(routed, handler(false).handle(call));
        assertSame(routed, container.getSingleton(ServiceResponseContract.class));
    }

    @Test
    void handleReturnsCallReceivedShortCircuit() {
        ServiceCallContract call = call(new CancellationToken());
        ServiceResponseContract shortCircuit = ServiceResponse.unimplemented("sc");
        when(callReceivedHandler.callReceived(any()))
                .thenReturn(new CallReceivedResult(call, shortCircuit));

        assertSame(shortCircuit, handler(false).handle(call));
        verify(router, never()).dispatch(any());
    }

    @Test
    void entryPreCheckCancellationSkipsPipeline() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);

        ServiceResponseContract result = handler(false).handle(call(token));

        assertEquals(StatusCode.DEADLINE_EXCEEDED, result.getStatus().getCode());
        verify(callReceivedHandler, never()).callReceived(any());
        verify(router, never()).dispatch(any());
    }

    @Test
    void thrownGenericThrowableMapsToInternalThenThrowableCaught() {
        ServiceCallContract call = call(new CancellationToken());
        when(callReceivedHandler.callReceived(any())).thenReturn(new CallReceivedResult(call, null));
        when(router.dispatch(any())).thenThrow(new IllegalStateException("boom"));
        when(throwableCaughtHandler.throwableCaught(any(), any(), any()))
                .thenAnswer(inv -> inv.getArgument(1));

        ServiceResponseContract result = handler(false).handle(call);

        assertEquals(StatusCode.INTERNAL, result.getStatus().getCode());
    }

    @Test
    void thrownCancelledExceptionMapsToCancellation() {
        ServiceCallContract call = call(new CancellationToken());
        when(callReceivedHandler.callReceived(any())).thenReturn(new CallReceivedResult(call, null));
        when(router.dispatch(any()))
                .thenThrow(new CancelledException("stop", CancellationReason.CLIENT_CANCELLED));
        when(throwableCaughtHandler.throwableCaught(any(), any(), any()))
                .thenAnswer(inv -> inv.getArgument(1));

        ServiceResponseContract result = handler(false).handle(call);

        assertEquals(StatusCode.CANCELLED, result.getStatus().getCode());
    }

    @Test
    void debugModeRethrowsInsteadOfMapping() {
        ServiceCallContract call = call(new CancellationToken());
        when(callReceivedHandler.callReceived(any())).thenReturn(new CallReceivedResult(call, null));
        when(router.dispatch(any())).thenThrow(new IllegalStateException("boom"));

        assertThrows(RuntimeException.class, () -> handler(true).handle(call));
    }

    @Test
    void sendingRunsSendingResponseStage() {
        ServiceCallContract call = call(new CancellationToken());
        ServiceResponseContract in = ServiceResponse.ok();
        ServiceResponseContract out = ServiceResponse.ok("sent");
        when(sendingResponseHandler.sendingResponse(call, in)).thenReturn(out);

        assertSame(out, handler(false).sending(call, in));
        assertSame(out, container.getSingleton(ServiceResponseContract.class));
    }

    @Test
    void terminateRunsResponseSentStage() {
        ServiceCallContract call = call(new CancellationToken());
        ServiceResponseContract response = ServiceResponse.ok();

        handler(false).terminate(call, response);

        verify(responseSentHandler).responseSent(call, response);
    }

    @Test
    void runBundlesHandleAndSending() {
        ServiceCallContract call = call(new CancellationToken());
        ServiceResponseContract routed = ServiceResponse.ok("routed");
        ServiceResponseContract sent = ServiceResponse.ok("sent");
        when(callReceivedHandler.callReceived(any())).thenReturn(new CallReceivedResult(call, null));
        when(router.dispatch(any())).thenReturn(routed);
        when(sendingResponseHandler.sendingResponse(any(), any())).thenReturn(sent);

        assertSame(sent, handler(false).run(call));
    }
}
