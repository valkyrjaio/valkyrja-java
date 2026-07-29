/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.grpc.endtoend;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.tests.fixtures.grpc.GreeterComponentProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * End-to-end test over a real gRPC HTTP/2 transport: a Netty server backed by {@link GrpcBridge}
 * and a Netty client, exercising both dispatch models — the buffered path (client-streaming {@code
 * Collect}) and the streaming path (bidirectional {@code Echo}, which runs on a per-call virtual
 * thread). Unlike the mock-based bridge tests, this drives the actual wire: framing, flow control,
 * headers, messages, and trailers.
 */
@Timeout(20)
final class GrpcNettyEndToEndTest {

    private static final MethodDescriptor.Marshaller<byte[]> BYTES =
            GrpcBridge.ByteMarshaller.INSTANCE;

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

    private Server startServer() throws IOException {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();
        return NettyServerBuilder.forPort(0)
                .fallbackHandlerRegistry(GrpcBridge.registry(app, data))
                .build()
                .start();
    }

    private static MethodDescriptor<byte[], byte[]> method(
            String fullMethodName, MethodDescriptor.MethodType type) {
        return MethodDescriptor.<byte[], byte[]>newBuilder()
                .setType(type)
                .setFullMethodName(fullMethodName)
                .setRequestMarshaller(BYTES)
                .setResponseMarshaller(BYTES)
                .build();
    }

    @Test
    void unaryCallReturnsTheSingleResponse() throws Exception {
        Server server = startServer();
        ManagedChannel channel =
                NettyChannelBuilder.forAddress("localhost", server.getPort())
                        .usePlaintext()
                        .build();
        try {
            List<byte[]> responses = Collections.synchronizedList(new ArrayList<>());
            AtomicReference<Status> status = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            ClientCall<byte[], byte[]> call =
                    channel.newCall(
                            method("pkg.Greeter/Ping", MethodDescriptor.MethodType.UNARY),
                            CallOptions.DEFAULT);
            call.start(collector(responses, status, done), new Metadata());
            call.request(1);
            call.sendMessage("ping".getBytes());
            call.halfClose();

            assertTrue(done.await(10, TimeUnit.SECONDS), "call did not complete");
            assertEquals(Status.Code.OK, status.get().getCode());
            assertEquals(1, responses.size());
            assertArrayEquals("pong".getBytes(), responses.get(0));
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    @Test
    void serverStreamingCallReturnsEveryMessageForOneRequest() throws Exception {
        Server server = startServer();
        ManagedChannel channel =
                NettyChannelBuilder.forAddress("localhost", server.getPort())
                        .usePlaintext()
                        .build();
        try {
            List<byte[]> responses = Collections.synchronizedList(new ArrayList<>());
            AtomicReference<Status> status = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            ClientCall<byte[], byte[]> call =
                    channel.newCall(
                            method(
                                    "pkg.Greeter/Fanout",
                                    MethodDescriptor.MethodType.SERVER_STREAMING),
                            CallOptions.DEFAULT);
            call.start(collector(responses, status, done), new Metadata());
            call.request(10);
            call.sendMessage("go".getBytes());
            call.halfClose();

            assertTrue(done.await(10, TimeUnit.SECONDS), "call did not complete");
            assertEquals(Status.Code.OK, status.get().getCode());
            assertEquals(3, responses.size());
            assertArrayEquals("x".getBytes(), responses.get(0));
            assertArrayEquals("y".getBytes(), responses.get(1));
            assertArrayEquals("z".getBytes(), responses.get(2));
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    @Test
    void bufferedClientStreamingCallReturnsTheHandlerResponse() throws Exception {
        Server server = startServer();
        ManagedChannel channel =
                NettyChannelBuilder.forAddress("localhost", server.getPort())
                        .usePlaintext()
                        .build();
        try {
            List<byte[]> responses = Collections.synchronizedList(new ArrayList<>());
            AtomicReference<Status> status = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            ClientCall<byte[], byte[]> call =
                    channel.newCall(
                            method(
                                    "pkg.Greeter/Collect",
                                    MethodDescriptor.MethodType.CLIENT_STREAMING),
                            CallOptions.DEFAULT);
            call.start(collector(responses, status, done), new Metadata());
            call.request(2);
            call.sendMessage("chunk-1".getBytes());
            call.sendMessage("chunk-2".getBytes());
            call.halfClose();

            assertTrue(done.await(10, TimeUnit.SECONDS), "call did not complete");
            assertEquals(Status.Code.OK, status.get().getCode());
            assertEquals(1, responses.size());
            assertArrayEquals("collected".getBytes(), responses.get(0));
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    @Test
    void bidirectionalStreamingCallEchoesEveryMessageOverTheWire() throws Exception {
        Server server = startServer();
        ManagedChannel channel =
                NettyChannelBuilder.forAddress("localhost", server.getPort())
                        .usePlaintext()
                        .build();
        try {
            List<byte[]> responses = Collections.synchronizedList(new ArrayList<>());
            AtomicReference<Status> status = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            ClientCall<byte[], byte[]> call =
                    channel.newCall(
                            method("pkg.Greeter/Echo", MethodDescriptor.MethodType.BIDI_STREAMING),
                            CallOptions.DEFAULT);
            call.start(collector(responses, status, done), new Metadata());
            call.request(10);
            call.sendMessage("a".getBytes());
            call.sendMessage("b".getBytes());
            call.sendMessage("c".getBytes());
            call.halfClose();

            assertTrue(done.await(10, TimeUnit.SECONDS), "call did not complete");
            assertEquals(Status.Code.OK, status.get().getCode());
            assertEquals(3, responses.size());
            assertArrayEquals("a".getBytes(), responses.get(0));
            assertArrayEquals("b".getBytes(), responses.get(1));
            assertArrayEquals("c".getBytes(), responses.get(2));
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    @Test
    void bidirectionalStreamingInterleavesEachEchoBeforeTheNextSend() throws Exception {
        Server server = startServer();
        ManagedChannel channel =
                NettyChannelBuilder.forAddress("localhost", server.getPort())
                        .usePlaintext()
                        .build();
        try {
            LinkedBlockingQueue<byte[]> echoes = new LinkedBlockingQueue<>();
            AtomicReference<Status> status = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            ClientCall<byte[], byte[]> call =
                    channel.newCall(
                            method("pkg.Greeter/Echo", MethodDescriptor.MethodType.BIDI_STREAMING),
                            CallOptions.DEFAULT);
            call.start(
                    new ClientCall.Listener<>() {
                        @Override
                        public void onMessage(byte[] message) {
                            echoes.add(message);
                        }

                        @Override
                        public void onClose(Status closeStatus, Metadata trailers) {
                            status.set(closeStatus);
                            done.countDown();
                        }
                    },
                    new Metadata());
            call.request(10);

            // Ping-pong: each message's echo MUST arrive before the next is sent, and crucially
            // before half-close. A buffered implementation cannot echo until half-close, so these
            // polls would time out — proving the path genuinely interleaves rather than buffers.
            call.sendMessage("a".getBytes());
            byte[] firstEcho = echoes.poll(10, TimeUnit.SECONDS);
            assertNotNull(
                    firstEcho, "server did not echo before half-close — the path is not streaming");
            assertArrayEquals("a".getBytes(), firstEcho);

            call.sendMessage("b".getBytes());
            byte[] secondEcho = echoes.poll(10, TimeUnit.SECONDS);
            assertNotNull(secondEcho, "server did not echo the second message before half-close");
            assertArrayEquals("b".getBytes(), secondEcho);

            call.halfClose();
            assertTrue(done.await(10, TimeUnit.SECONDS), "call did not complete");
            assertEquals(Status.Code.OK, status.get().getCode());
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    private static ClientCall.Listener<byte[]> collector(
            List<byte[]> responses, AtomicReference<Status> status, CountDownLatch done) {
        return new ClientCall.Listener<>() {
            @Override
            public void onMessage(byte[] message) {
                responses.add(message);
            }

            @Override
            public void onClose(Status closeStatus, Metadata trailers) {
                status.set(closeStatus);
                done.countDown();
            }
        };
    }
}
