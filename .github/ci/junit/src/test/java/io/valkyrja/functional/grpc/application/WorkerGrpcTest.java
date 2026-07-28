/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.grpc.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.fixtures.grpc.GreeterComponentProvider;
import io.valkyrja.fixtures.grpc.GreeterController;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.message.stream.InboundMessageStream;
import io.valkyrja.grpc.message.stream.contract.OutboundStreamContract;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/** Test the {@link WorkerGrpc} persistent-worker entry point. */
final class WorkerGrpcTest {

    private GrpcConfig config() {
        return new GrpcConfig(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/grpc/provider/data",
                "app.grpc.provider.data",
                50051,
                1000,
                List.of(new GreeterComponentProvider()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    @Test
    void bootstrapThenDispatchWritesResponseBeforeTerminate() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        AtomicReference<ServiceResponseContract> written = new AtomicReference<>();
        WorkerGrpc.dispatch(
                app, data, ServiceCall.unary("/pkg.Greeter/SayHello", "req"), written::set);

        ServiceResponseContract response = written.get();
        assertNotNull(response);
        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void dispatchRunsResponseSentEvenWhenTheWriterThrows() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        // A wire write that blows up must not skip the ResponseSent stage, or per-call resources
        // leak and observers never see the call complete.
        GreeterController.ResponseSentMiddleware.calls.set(0);
        assertThrows(
                IllegalStateException.class,
                () ->
                        WorkerGrpc.dispatch(
                                app,
                                data,
                                ServiceCall.unary("/pkg.Greeter/StreamHellos", "req"),
                                response -> {
                                    throw new IllegalStateException("wire write failed");
                                }));

        assertEquals(1, GreeterController.ResponseSentMiddleware.calls.get());
    }

    @Test
    void dispatchIsolatesEachCallInAChildContainer() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        AtomicReference<ServiceResponseContract> first = new AtomicReference<>();
        AtomicReference<ServiceResponseContract> second = new AtomicReference<>();
        WorkerGrpc.dispatch(app, data, ServiceCall.unary("/pkg.Greeter/SayHello", "a"), first::set);
        WorkerGrpc.dispatch(app, data, ServiceCall.unary("/pkg.Greeter/Missing", "b"), second::set);

        assertTrue(first.get().getStatus().isOk());
        assertEquals(StatusCode.UNIMPLEMENTED, second.get().getStatus().getCode());
    }

    @Test
    void isInstantiableViaSubclass() {
        assertNotNull(new WorkerGrpc() {});
    }

    @Test
    void lifecycleHelpersAreReusableStandalone() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        var child = WorkerGrpc.getChildContainer(app, data);
        var childApp = WorkerGrpc.getChildApplication(app, child);
        WorkerGrpc.bootstrapChildContainer(childApp, child);

        assertNotNull(
                child.getSingleton(
                        io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract.class));
    }

    @Test
    void dispatchStreamingEchoesInboundAndFiresMiddlewareOnceAtOpenAndClose() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        GreeterController.SendingMiddleware.calls.set(0);
        GreeterController.ResponseSentMiddleware.calls.set(0);

        InboundMessageStream inbound = new InboundMessageStream();
        inbound.offer("a");
        inbound.offer("b");
        inbound.complete();

        RecordingStream outbound = new RecordingStream();
        WorkerGrpc.dispatchStreaming(
                app, data, streamingCall("/pkg.Greeter/Echo", inbound), outbound);

        assertEquals(List.of("a", "b"), outbound.messages);
        assertTrue(outbound.headersSent);
        assertNotNull(outbound.terminal);
        assertEquals(StatusCode.OK, outbound.terminal.getStatus().getCode());
        // SendingResponse once at stream open (first emit), ResponseSent once at close.
        assertEquals(1, GreeterController.SendingMiddleware.calls.get());
        assertEquals(1, GreeterController.ResponseSentMiddleware.calls.get());
    }

    @Test
    void dispatchStreamingOpensAndClosesEvenWhenTheHandlerEmitsNothing() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        GreeterController.SendingMiddleware.calls.set(0);
        GreeterController.ResponseSentMiddleware.calls.set(0);

        InboundMessageStream inbound = new InboundMessageStream();
        inbound.complete();

        RecordingStream outbound = new RecordingStream();
        WorkerGrpc.dispatchStreaming(
                app, data, streamingCall("/pkg.Greeter/StreamHellos", inbound), outbound);

        // Nothing emitted, but the stream still opens (headers) and closes, and each stage fires
        // once.
        assertTrue(outbound.messages.isEmpty());
        assertTrue(outbound.headersSent);
        assertNotNull(outbound.terminal);
        assertEquals(1, GreeterController.SendingMiddleware.calls.get());
        assertEquals(1, GreeterController.ResponseSentMiddleware.calls.get());
    }

    private static java.util.function.Function<
                    java.util.function.Consumer<Object>,
                    io.valkyrja.grpc.message.call.contract.ServiceCallContract>
            streamingCall(String method, InboundMessageStream inbound) {
        return sink ->
                new ServiceCall(
                        method,
                        new Metadata(),
                        Deadline.none(),
                        CancellationToken.never(),
                        Peer.insecure("x"),
                        inbound,
                        null,
                        sink);
    }

    /** Records what the streaming dispatch pushes to the transport. */
    private static final class RecordingStream implements OutboundStreamContract {
        final List<Object> messages = new ArrayList<>();
        boolean headersSent;
        @Nullable ServiceResponseContract terminal;

        @Override
        public void sendHeaders(MetadataContract initialMetadata) {
            headersSent = true;
        }

        @Override
        public void sendMessage(Object message) {
            messages.add(message);
        }

        @Override
        public void close(ServiceResponseContract terminalResponse) {
            this.terminal = terminalResponse;
        }
    }
}
