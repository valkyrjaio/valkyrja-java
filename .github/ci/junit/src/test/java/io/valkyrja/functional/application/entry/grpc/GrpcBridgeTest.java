/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.grpc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import io.valkyrja.fixtures.grpc.GreeterComponentProvider;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.response.ServiceResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcBridge} grpc-java translation layer. */
final class GrpcBridgeTest {

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

    @SuppressWarnings("unchecked")
    private ServerCall<byte[], byte[]> mockCall() {
        return mock(ServerCall.class);
    }

    @Test
    void registryLookupBuildsAByteMethodDefinition() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        HandlerRegistry registry = GrpcBridge.registry(app, data);
        ServerMethodDefinition<?, ?> definition =
                registry.lookupMethod("pkg.Greeter/SayHello", null);

        assertNotNull(definition);
        assertEquals(
                "pkg.Greeter/SayHello", definition.getMethodDescriptor().getFullMethodName());
    }

    @Test
    void buildCallPopulatesTheServiceCall() {
        ServerCall<byte[], byte[]> call = mockCall();
        when(call.getAttributes())
                .thenReturn(
                        Attributes.newBuilder()
                                .set(
                                        Grpc.TRANSPORT_ATTR_REMOTE_ADDR,
                                        new InetSocketAddress("1.2.3.4", 4242))
                                .build());

        ServiceCallContract serviceCall =
                GrpcBridge.buildCall(call, "pkg.Svc/M", List.of("a".getBytes()));

        assertEquals("/pkg.Svc/M", serviceCall.getMethod());
        assertNotNull(serviceCall.getPeer());
        assertTrue(serviceCall.getMessages().iterator().hasNext());
    }

    @Test
    void deadlineIsNoneWithoutAContextDeadline() {
        assertTrue(GrpcBridge.deadline().getRemaining().compareTo(Duration.ofDays(3650)) > 0);
    }

    @Test
    void deadlineReflectsAContextDeadline() throws Exception {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
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
    void writeSendsHeadersMessagesAndStatus() {
        ServerCall<byte[], byte[]> call = mockCall();
        ServiceCallContract serviceCall = ServiceCall.unary("/pkg.Svc/M", "req");
        ServiceResponse response =
                (ServiceResponse)
                        ServiceResponse.ok("hi".getBytes())
                                .withInitialMetadata(new Metadata().with("x", "v"));

        GrpcBridge.write(call, serviceCall, response);

        verify(call).sendHeaders(any());
        verify(call).sendMessage(any());
        verify(call)
                .close(
                        argThat(status -> status.getCode() == io.grpc.Status.Code.OK),
                        any());
    }

    @Test
    void toGrpcMetadataSkipsBinaryKeys() {
        Metadata metadata =
                (Metadata) new Metadata().with("x", "v").with("y-bin", "binary".getBytes());

        io.grpc.Metadata grpcMetadata = GrpcBridge.toGrpcMetadata(metadata);

        // The binary key is iterated but skipped; only the ASCII key survives.
        assertTrue(grpcMetadata.keys().contains("x"));
        assertFalse(grpcMetadata.keys().contains("y-bin"));
    }

    @Test
    void byteMarshallerRoundTrips() throws Exception {
        byte[] bytes = "payload".getBytes();

        try (InputStream stream = GrpcBridge.ByteMarshaller.INSTANCE.stream(bytes)) {
            assertArrayEquals(bytes, stream.readAllBytes());
        }
        assertArrayEquals(
                bytes,
                GrpcBridge.ByteMarshaller.INSTANCE.parse(
                        new java.io.ByteArrayInputStream(bytes)));
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
    void handlerBuffersMessagesAndDispatchesOnHalfClose() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        ServerCall<byte[], byte[]> call = mockCall();
        when(call.getAttributes()).thenReturn(Attributes.EMPTY);

        ServerCallHandler<byte[], byte[]> handler =
                GrpcBridge.handler(app, data, "pkg.Greeter/Missing");
        ServerCall.Listener<byte[]> listener = handler.startCall(call, new io.grpc.Metadata());
        listener.onMessage("ignored".getBytes());
        listener.onHalfClose();

        verify(call).request(Integer.MAX_VALUE);
        // The unknown method dispatches to UNIMPLEMENTED — closed, no message written.
        verify(call)
                .close(
                        argThat(status -> status.getCode() == io.grpc.Status.Code.UNIMPLEMENTED),
                        any());
        verify(call, never()).sendMessage(any());
    }

    @Test
    void isNotInstantiablePublicly() throws Exception {
        var constructor = GrpcBridge.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
