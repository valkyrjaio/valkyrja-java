/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.grpc;

import io.grpc.HandlerRegistry;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The transport-agnostic grpc-java bridge: it turns any inbound gRPC method into a call on {@link
 * WorkerGrpc#dispatch}. Depends only on the {@code io.grpc.*} API, so it is shared verbatim by
 * every grpc-java transport (Netty, servlet, …).
 *
 * <p>Messages cross the boundary as raw {@code byte[]} — the framework stays worker-agnostic and
 * never references generated protobuf types; user handlers decode and encode as needed.
 */
public final class GrpcBridge {

    private GrpcBridge() {}

    /**
     * Build the generic fallback registry that maps any inbound method to {@link
     * WorkerGrpc#dispatch}.
     *
     * @param app the frozen parent application
     * @param data the container data snapshot
     * @return the fallback handler registry
     */
    public static HandlerRegistry registry(ApplicationContract app, ContainerData data) {
        return new HandlerRegistry() {
            @Override
            public ServerMethodDefinition<byte[], byte[]> lookupMethod(
                    String fullMethodName, String authority) {
                MethodDescriptor<byte[], byte[]> descriptor =
                        MethodDescriptor.<byte[], byte[]>newBuilder()
                                .setType(MethodDescriptor.MethodType.UNKNOWN)
                                .setFullMethodName(fullMethodName)
                                .setRequestMarshaller(ByteMarshaller.INSTANCE)
                                .setResponseMarshaller(ByteMarshaller.INSTANCE)
                                .build();

                return ServerMethodDefinition.create(
                        descriptor, handler(app, data, fullMethodName));
            }
        };
    }

    /**
     * A call handler that buffers inbound messages, dispatches on half-close, and writes the
     * response back to the wire.
     *
     * @param app the frozen parent application
     * @param data the container data snapshot
     * @param fullMethodName the grpc method name (no leading slash)
     * @return the server call handler
     */
    public static ServerCallHandler<byte[], byte[]> handler(
            ApplicationContract app, ContainerData data, String fullMethodName) {
        return (call, headers) -> {
            call.request(Integer.MAX_VALUE);
            List<Object> messages = new ArrayList<>();

            return new ServerCall.Listener<>() {
                @Override
                public void onMessage(byte[] message) {
                    messages.add(message);
                }

                @Override
                public void onHalfClose() {
                    ServiceCallContract serviceCall = buildCall(call, fullMethodName, messages);
                    WorkerGrpc.dispatch(
                            app, data, serviceCall, response -> write(call, serviceCall, response));
                }
            };
        };
    }

    /**
     * Build a {@link ServiceCall} from the native call, keying the framework method with a leading
     * slash ({@code /package.Service/Method}).
     *
     * @param call the native server call
     * @param fullMethodName the grpc method name (no leading slash)
     * @param messages the buffered inbound messages
     * @return the service call
     */
    public static ServiceCallContract buildCall(
            ServerCall<byte[], byte[]> call, String fullMethodName, List<Object> messages) {
        String address =
                String.valueOf(call.getAttributes().get(io.grpc.Grpc.TRANSPORT_ATTR_REMOTE_ADDR));

        return new ServiceCall(
                "/" + fullMethodName,
                new Metadata(),
                deadline(),
                new CancellationToken(),
                Peer.insecure(address),
                messages,
                null);
    }

    /**
     * Translate the current gRPC context deadline into a {@link Deadline}.
     *
     * @return the deadline, or {@code Deadline.none()} when the client set none
     */
    public static Deadline deadline() {
        io.grpc.Deadline grpcDeadline = io.grpc.Context.current().getDeadline();

        if (grpcDeadline == null) {
            return Deadline.none();
        }

        return Deadline.fromTimeout(
                Duration.ofNanos(grpcDeadline.timeRemaining(TimeUnit.NANOSECONDS)));
    }

    /**
     * Write a {@link ServiceResponseContract} to the wire: initial headers, each message (drained
     * through the per-step cancellation check), then the status and trailers.
     *
     * @param call the native server call
     * @param serviceCall the inbound call (for the cancellation check)
     * @param response the response to write
     */
    public static void write(
            ServerCall<byte[], byte[]> call,
            ServiceCallContract serviceCall,
            ServiceResponseContract response) {
        call.sendHeaders(toGrpcMetadata(response.getInitialMetadata()));

        for (Object message : serviceCall.cancellable(response.getMessages())) {
            call.sendMessage((byte[]) message);
        }

        io.grpc.Status status =
                io.grpc.Status.fromCodeValue(response.getStatus().getCode().getValue())
                        .withDescription(response.getStatus().getMessage());

        call.close(status, toGrpcMetadata(response.getTrailingMetadata()));
    }

    /**
     * Translate framework metadata to gRPC metadata (ASCII values only; binary handling is
     * deferred).
     *
     * @param metadata the framework metadata
     * @return the gRPC metadata
     */
    public static io.grpc.Metadata toGrpcMetadata(MetadataContract metadata) {
        io.grpc.Metadata grpcMetadata = new io.grpc.Metadata();

        for (var entry : metadata) {
            if (metadata.isBinaryKey(entry.getKey())) {
                continue;
            }

            io.grpc.Metadata.Key<String> key =
                    io.grpc.Metadata.Key.of(
                            entry.getKey(), io.grpc.Metadata.ASCII_STRING_MARSHALLER);

            for (Object value : entry.getValue()) {
                grpcMetadata.put(key, String.valueOf(value));
            }
        }

        return grpcMetadata;
    }

    /** Identity marshaller: gRPC message bytes pass through untouched. */
    public static final class ByteMarshaller implements MethodDescriptor.Marshaller<byte[]> {

        public static final ByteMarshaller INSTANCE = new ByteMarshaller();

        @Override
        public InputStream stream(byte[] value) {
            return new ByteArrayInputStream(value);
        }

        @Override
        public byte[] parse(InputStream stream) {
            try {
                return stream.readAllBytes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
