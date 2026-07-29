/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.application.entry.grpc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.grpc.Attributes;
import io.grpc.Context;
import io.grpc.Grpc;
import io.grpc.HandlerRegistry;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.contract.PeerContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.status.Status;
import io.valkyrja.tests.fixtures.grpc.GreeterComponentProviderFixture;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;

/** Test the {@link GrpcBridge} grpc-java translation layer. */
final class GrpcBridgeTest {

    private GrpcConfig config() {
        return config(1000);
    }

    private GrpcConfig config(int maxInboundMessages) {
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
                maxInboundMessages,
                List.of(new GreeterComponentProviderFixture()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    @SuppressWarnings("unchecked")
    private ServerCall<byte[], byte[]> mockCall() {
        return mock(ServerCall.class);
    }

    private ServerCall<byte[], byte[]> mockCall(SocketAddressAndSession attributes) {
        ServerCall<byte[], byte[]> call = mockCall();
        Attributes.Builder builder = Attributes.newBuilder();
        if (attributes.remote() != null) {
            builder.set(Grpc.TRANSPORT_ATTR_REMOTE_ADDR, attributes.remote());
        }
        if (attributes.session() != null) {
            builder.set(Grpc.TRANSPORT_ATTR_SSL_SESSION, attributes.session());
        }
        when(call.getAttributes()).thenReturn(builder.build());
        return call;
    }

    private record SocketAddressAndSession(InetSocketAddress remote, SSLSession session) {}

    @Test
    void registryLookupBuildsAByteMethodDefinition() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        HandlerRegistry registry = GrpcBridge.registry(app, data);
        ServerMethodDefinition<?, ?> definition =
                registry.lookupMethod("pkg.Greeter/SayHello", null);

        assertNotNull(definition);
        assertEquals("pkg.Greeter/SayHello", definition.getMethodDescriptor().getFullMethodName());
    }

    @Test
    void buildCallCarriesHeadersDeadlineTokenAndPeer() {
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 4242), null));
        io.grpc.Metadata headers = new io.grpc.Metadata();
        headers.put(
                io.grpc.Metadata.Key.of("authorization", io.grpc.Metadata.ASCII_STRING_MARSHALLER),
                "token");

        ServiceCallContract serviceCall =
                GrpcBridge.buildCall(
                        call,
                        headers,
                        "pkg.Svc/M",
                        List.of("a".getBytes()),
                        new CancellationToken());

        assertEquals("/pkg.Svc/M", serviceCall.getMethod());
        assertEquals("token", serviceCall.getMetadata().get("authorization"));
        assertNotNull(serviceCall.getPeer());
        assertTrue(serviceCall.getMessages().iterator().hasNext());
    }

    @Test
    void peerReportsTlsAndIpv4() {
        ServerCall<byte[], byte[]> call =
                mockCall(
                        new SocketAddressAndSession(
                                new InetSocketAddress("1.2.3.4", 80), mock(SSLSession.class)));

        PeerContract peer = GrpcBridge.peer(call);

        assertEquals(AddressType.IPV4, peer.getAddressType());
        assertEquals("tls", peer.getAuthContext().getType());
    }

    @Test
    void peerReportsInsecureIpv6() throws Exception {
        ServerCall<byte[], byte[]> call =
                mockCall(
                        new SocketAddressAndSession(
                                new InetSocketAddress(InetAddress.getByName("::1"), 80), null));

        PeerContract peer = GrpcBridge.peer(call);

        assertEquals(AddressType.IPV6, peer.getAddressType());
        assertEquals("insecure", peer.getAuthContext().getType());
    }

    @Test
    void peerReportsUnknownAddressTypeForAnUnresolvedAddress() {
        ServerCall<byte[], byte[]> call =
                mockCall(
                        new SocketAddressAndSession(
                                InetSocketAddress.createUnresolved("host", 80), null));

        assertEquals(AddressType.UNKNOWN, GrpcBridge.peer(call).getAddressType());
    }

    @Test
    void wireCancellationFiresTheTokenOffTheSerializedListenerPath() {
        Context.CancellableContext context = Context.current().withCancellation();
        CancellationToken token = new CancellationToken();

        GrpcBridge.wireCancellation(token, context);
        context.cancel(new RuntimeException("client gone"));

        assertTrue(token.isCancelled());
        assertEquals(CancellationReason.CLIENT_CANCELLED, token.getReason());
    }

    @Test
    void peerReportsUnknownAddressTypeForANullRemote() {
        ServerCall<byte[], byte[]> call = mockCall(new SocketAddressAndSession(null, null));

        assertEquals(AddressType.UNKNOWN, GrpcBridge.peer(call).getAddressType());
    }

    @Test
    void cancellationReasonClassifiesDeadlineExpiryVersusClientCancel() {
        assertEquals(
                CancellationReason.DEADLINE_EXCEEDED,
                GrpcBridge.cancellationReason(io.grpc.Deadline.after(-1, TimeUnit.SECONDS)));
        assertEquals(
                CancellationReason.CLIENT_CANCELLED,
                GrpcBridge.cancellationReason(io.grpc.Deadline.after(1, TimeUnit.HOURS)));
        assertEquals(CancellationReason.CLIENT_CANCELLED, GrpcBridge.cancellationReason(null));
    }

    @Test
    void deadlineIsNoneWithoutAContextDeadline() {
        assertTrue(GrpcBridge.deadline().getRemaining().compareTo(Duration.ofDays(3650)) > 0);
    }

    @Test
    void deadlineReflectsAContextDeadline() throws Exception {
        var executor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        try {
            Deadline deadline =
                    Context.current()
                            .withDeadline(io.grpc.Deadline.after(5, TimeUnit.SECONDS), executor)
                            .call(GrpcBridge::deadline);

            assertTrue(deadline.getRemaining().compareTo(Duration.ofMinutes(1)) < 0);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void writeSendsHeadersMessagesStatusAndDetails() {
        ServerCall<byte[], byte[]> call = mockCall();
        ServiceCallContract serviceCall = ServiceCall.unary("/pkg.Svc/M", "req");
        ServiceResponse response =
                (ServiceResponse)
                        ServiceResponse.of(Status.internal("boom", "detail".getBytes()))
                                .withMessages(List.of("hi".getBytes()))
                                .withInitialMetadata(new Metadata().with("x", "v"));

        ArgumentCaptor<io.grpc.Metadata> trailers = ArgumentCaptor.forClass(io.grpc.Metadata.class);
        GrpcBridge.write(call, serviceCall, response);

        verify(call).sendHeaders(any());
        verify(call).sendMessage(any());
        verify(call)
                .close(
                        argThat(status -> status.getCode() == io.grpc.Status.Code.INTERNAL),
                        trailers.capture());
        assertTrue(trailers.getValue().keys().contains("grpc-status-details-bin"));
    }

    @Test
    void writeOverwritesAHandlerSetStatusDetailsTrailer() {
        ServerCall<byte[], byte[]> call = mockCall();
        ServiceCallContract serviceCall = ServiceCall.unary("/pkg.Svc/M", "req");
        ServiceResponse response =
                (ServiceResponse)
                        ServiceResponse.of(Status.internal("boom", "authoritative".getBytes()))
                                .withTrailingMetadata(
                                        new Metadata()
                                                .with(
                                                        "grpc-status-details-bin",
                                                        "handler-set".getBytes()));

        ArgumentCaptor<io.grpc.Metadata> trailers = ArgumentCaptor.forClass(io.grpc.Metadata.class);
        GrpcBridge.write(call, serviceCall, response);
        verify(call).close(any(), trailers.capture());

        List<byte[]> details = new java.util.ArrayList<>();
        trailers.getValue()
                .getAll(
                        io.grpc.Metadata.Key.of(
                                "grpc-status-details-bin", io.grpc.Metadata.BINARY_BYTE_MARSHALLER))
                .forEach(details::add);
        // Only the Status's own details survive; the handler-set duplicate was discarded.
        assertEquals(1, details.size());
        assertArrayEquals("authoritative".getBytes(), details.get(0));
    }

    @Test
    void toAndFromGrpcMetadataCarryAsciiAndBinary() {
        Metadata metadata =
                (Metadata) new Metadata().with("x", "v").with("y-bin", "binary".getBytes());

        io.grpc.Metadata grpcMetadata = GrpcBridge.toGrpcMetadata(metadata);
        assertTrue(grpcMetadata.keys().contains("x"));
        assertTrue(grpcMetadata.keys().contains("y-bin"));

        MetadataContract roundTrip = GrpcBridge.fromGrpcMetadata(grpcMetadata);
        assertEquals("v", roundTrip.get("x"));
        assertArrayEquals("binary".getBytes(), (byte[]) roundTrip.get("y-bin"));
    }

    @Test
    void byteMarshallerRoundTrips() throws Exception {
        byte[] bytes = "payload".getBytes();

        try (InputStream stream = GrpcBridge.ByteMarshaller.INSTANCE.stream(bytes)) {
            assertArrayEquals(bytes, stream.readAllBytes());
        }
        assertArrayEquals(
                bytes,
                GrpcBridge.ByteMarshaller.INSTANCE.parse(new java.io.ByteArrayInputStream(bytes)));
    }

    @Test
    void byteMarshallerParseWrapsIoException() {
        InputStream failing =
                new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("boom");
                    }
                };

        assertThrows(
                java.io.UncheckedIOException.class,
                () -> GrpcBridge.ByteMarshaller.INSTANCE.parse(failing));
    }

    @Test
    void handlerBuffersUnderFlowControlAndDispatchesOnHalfClose() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        ServerCallHandler<byte[], byte[]> handler =
                GrpcBridge.handler(app, data, "pkg.Greeter/Missing");
        ServerCall.Listener<byte[]> listener = handler.startCall(call, new io.grpc.Metadata());
        listener.onMessage("ignored".getBytes());
        listener.onHalfClose();
        listener.onCancel();

        verify(call, org.mockito.Mockito.atLeastOnce()).request(1);
        // The unknown method dispatches to UNIMPLEMENTED — closed, no message written.
        verify(call)
                .close(
                        argThat(status -> status.getCode() == io.grpc.Status.Code.UNIMPLEMENTED),
                        any());
        verify(call, never()).sendMessage(any());
    }

    @Test
    void handlerRejectsWhenTheInboundBufferOverflows() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call = mockCall();

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/SayHello")
                        .startCall(call, new io.grpc.Metadata());
        for (int i = 0; i <= 1000; i++) {
            listener.onMessage(new byte[] {1});
        }
        // Any further delivery after the overflow-close must be ignored, not re-close the call.
        listener.onMessage(new byte[] {1});
        // A half-close after the overflow must not run the pipeline against the closed call.
        listener.onHalfClose();

        // Closed exactly once (RESOURCE_EXHAUSTED) — the post-reject message did not close again.
        verify(call, times(1))
                .close(
                        argThat(
                                status ->
                                        status.getCode() == io.grpc.Status.Code.RESOURCE_EXHAUSTED),
                        any());
        verify(call, never()).sendHeaders(any());
    }

    @Test
    void handlerHonorsAConfiguredInboundLimit() {
        // A GrpcConfig with a lower maxInboundMessages rejects sooner than the 1000 default.
        ApplicationContract app = WorkerGrpc.bootstrap(config(2));
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call = mockCall();

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/SayHello")
                        .startCall(call, new io.grpc.Metadata());
        listener.onMessage(new byte[] {1});
        listener.onMessage(new byte[] {1});
        listener.onMessage(new byte[] {1});

        verify(call)
                .close(
                        argThat(
                                status ->
                                        status.getCode() == io.grpc.Status.Code.RESOURCE_EXHAUSTED),
                        any());
    }

    @Test
    void handlerBuffersAClientStreamingMethodRatherThanStreamingIt() {
        // Client-streaming (not bidirectional) takes the buffered path: buffer, dispatch on
        // half-close, write one response.
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/Collect")
                        .startCall(call, new io.grpc.Metadata());
        listener.onMessage("a".getBytes());
        listener.onMessage("b".getBytes());
        listener.onHalfClose();

        verify(call).sendHeaders(any());
        verify(call).sendMessage(any());
        verify(call).close(argThat(status -> status.getCode() == io.grpc.Status.Code.OK), any());
    }

    @Test
    @Timeout(5)
    void streamingOnCancelCompletesTheInboundSoTheWorkerThreadFinishes()
            throws InterruptedException {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        CountDownLatch closed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            closed.countDown();
                            return null;
                        })
                .when(call)
                .close(any(), any());

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/Echo")
                        .startCall(call, new io.grpc.Metadata());
        // Cancel before any message: the inbound completes, the echo handler's read loop ends, and
        // the worker thread finishes and closes the call rather than hanging on the deadline.
        listener.onCancel();

        assertTrue(closed.await(5, TimeUnit.SECONDS));
    }

    @Test
    @Timeout(5)
    void streamingRequestsTheHighWaterUpFrontAndRefillsOnePerDrainedMessage()
            throws InterruptedException {
        ApplicationContract app = WorkerGrpc.bootstrap(config(1000));
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        CountDownLatch closed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            closed.countDown();
                            return null;
                        })
                .when(call)
                .close(any(), any());

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/Echo")
                        .startCall(call, new io.grpc.Metadata());
        listener.onMessage("a".getBytes());
        listener.onMessage("b".getBytes());
        listener.onMessage("c".getBytes());
        listener.onHalfClose();

        assertTrue(closed.await(5, TimeUnit.SECONDS));
        // One up-front request for the whole high-water window, then one refill per drained
        // message.
        // That is what keeps the inbound queue bounded — it never grows past maxInboundMessages.
        verify(call).request(1000);
        verify(call, times(3)).request(1);
    }

    @Test
    @Timeout(5)
    void streamingCancelMidStreamAfterAMessageUnwindsTheHandlerAndCloses()
            throws InterruptedException {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        CountDownLatch echoed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            echoed.countDown();
                            return null;
                        })
                .when(call)
                .sendMessage(any());
        CountDownLatch closed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            closed.countDown();
                            return null;
                        })
                .when(call)
                .close(any(), any());

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/Echo")
                        .startCall(call, new io.grpc.Metadata());
        listener.onMessage("a".getBytes());
        // Wait until the handler has drained and echoed "a" — it is now mid-stream, parked on the
        // next read — then cancel before half-close.
        assertTrue(echoed.await(5, TimeUnit.SECONDS));
        listener.onCancel();

        // The inbound completes, the handler unwinds, and the call closes — exactly one echo sent.
        assertTrue(closed.await(5, TimeUnit.SECONDS));
        verify(call).sendMessage(any());
    }

    @Test
    @Timeout(5)
    void handlerStreamsBidirectionallyEchoingEachMessageThroughAWorkerThread()
            throws InterruptedException {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call =
                mockCall(new SocketAddressAndSession(new InetSocketAddress("1.2.3.4", 80), null));

        // The echo runs on a per-call virtual thread; latch on close to await its completion.
        CountDownLatch closed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            closed.countDown();
                            return null;
                        })
                .when(call)
                .close(any(), any());

        ServerCall.Listener<byte[]> listener =
                GrpcBridge.handler(app, data, "pkg.Greeter/Echo")
                        .startCall(call, new io.grpc.Metadata());
        listener.onMessage("a".getBytes());
        listener.onMessage("b".getBytes());
        listener.onHalfClose();

        assertTrue(closed.await(5, TimeUnit.SECONDS));
        // Headers committed once at open, each inbound echoed back, closed OK.
        verify(call).sendHeaders(any());
        verify(call, times(2)).sendMessage(any());
        verify(call).close(argThat(status -> status.getCode() == io.grpc.Status.Code.OK), any());
    }

    @Test
    void isNotInstantiablePublicly() throws Exception {
        var constructor = GrpcBridge.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
