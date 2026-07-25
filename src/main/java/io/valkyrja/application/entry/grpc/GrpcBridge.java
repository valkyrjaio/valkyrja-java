/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.grpc;

import io.grpc.Context;
import io.grpc.Grpc;
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
import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.AuthContext;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;

/**
 * The transport-agnostic grpc-java bridge: it turns any inbound gRPC method into a call on {@link
 * WorkerGrpc#dispatch}. Depends only on the {@code io.grpc.*} API (an optional/compileOnly
 * dependency of core), so it is shared verbatim by every grpc-java transport (Netty, servlet, …).
 *
 * <p>Messages cross the boundary as raw {@code byte[]} — the framework stays worker-agnostic and
 * never references generated protobuf types; user handlers decode and encode as needed.
 */
public final class GrpcBridge {

    /**
     * Upper bound on the messages buffered for one call before it is rejected. The framework
     * buffers the full inbound stream before dispatching, so an unbounded client-streaming call
     * would otherwise exhaust memory; this caps it and returns {@code RESOURCE_EXHAUSTED}.
     */
    private static final int MAX_INBOUND_MESSAGES = 1000;

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
     * A call handler that buffers inbound messages under flow control, propagates cancellation and
     * deadline expiry to a {@link CancellationToken}, dispatches on half-close, and writes the
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
            // Created up front and shared with the listener so cancellation (which can arrive
            // before half-close) has something to fire.
            CancellationToken token = new CancellationToken();

            // Fire the token from context cancellation. Listener callbacks (onCancel) are
            // serialized per call, so onCancel cannot preempt a handler running inside onHalfClose;
            // the context is cancelled off that serialized path (deadline scheduler / transport
            // thread), so a cooperative handler polling throwIfCancelled() actually observes it
            // mid-flight. onCancel below remains as a belt-and-suspenders fallback.
            wireCancellation(token, Context.current());

            // Ask for one message at a time so the transport applies backpressure instead of the
            // client flooding the server.
            call.request(1);
            List<Object> messages = new ArrayList<>();
            boolean[] rejected = {false};

            return new ServerCall.Listener<>() {
                @Override
                public void onMessage(byte[] message) {
                    if (messages.size() >= MAX_INBOUND_MESSAGES) {
                        rejected[0] = true;
                        call.close(
                                io.grpc.Status.RESOURCE_EXHAUSTED.withDescription(
                                        "Inbound message limit exceeded."),
                                new io.grpc.Metadata());
                        return;
                    }

                    messages.add(message);
                    call.request(1);
                }

                @Override
                public void onHalfClose() {
                    // The overflow path already closed the call; don't run the pipeline against a
                    // closed call (the handler would run for nothing and the write would throw).
                    if (rejected[0]) {
                        return;
                    }

                    ServiceCallContract serviceCall =
                            buildCall(call, headers, fullMethodName, messages, token);
                    WorkerGrpc.dispatch(
                            app, data, serviceCall, response -> write(call, serviceCall, response));
                }

                @Override
                public void onCancel() {
                    token.cancel(cancellationReason(Context.current().getDeadline()));
                }
            };
        };
    }

    /**
     * Fire the token when the call's context is cancelled — a client cancel, a reset stream, or a
     * lapsed deadline. Runs off the serialized listener path, so it can transition the token while
     * a synchronous handler is executing.
     *
     * <p>Note: {@link Context}'s listener also fires on normal completion, so this cancels the
     * token once the call ends regardless of outcome. That is harmless today because the token is
     * consumed only while the handler runs; a future stage that inspects the token after the
     * response is sent would need to distinguish a genuine cancel from ordinary completion.
     *
     * @param token the shared cancellation token
     * @param context the call's context
     */
    public static void wireCancellation(CancellationToken token, Context context) {
        context.addListener(
                cancelled -> token.cancel(cancellationReason(cancelled.getDeadline())),
                Runnable::run);
    }

    /**
     * Classify a cancel callback: an expired deadline is {@code DEADLINE_EXCEEDED}, anything else
     * (an explicit client cancel, a reset stream) is {@code CLIENT_CANCELLED}.
     *
     * @param deadline the call's deadline, or null when none was set
     * @return the cancellation reason
     */
    public static CancellationReason cancellationReason(io.grpc.Deadline deadline) {
        return deadline != null && deadline.isExpired()
                ? CancellationReason.DEADLINE_EXCEEDED
                : CancellationReason.CLIENT_CANCELLED;
    }

    /**
     * Build a {@link ServiceCall} from the native call, keying the framework method with a leading
     * slash ({@code /package.Service/Method}) and carrying the inbound headers, deadline, shared
     * cancellation token, and peer.
     *
     * @param call the native server call
     * @param headers the inbound metadata
     * @param fullMethodName the grpc method name (no leading slash)
     * @param messages the buffered inbound messages
     * @param token the shared cancellation token
     * @return the service call
     */
    public static ServiceCallContract buildCall(
            ServerCall<byte[], byte[]> call,
            io.grpc.Metadata headers,
            String fullMethodName,
            List<Object> messages,
            CancellationToken token) {
        return new ServiceCall(
                "/" + fullMethodName,
                fromGrpcMetadata(headers),
                deadline(),
                token,
                peer(call),
                messages,
                null);
    }

    /**
     * Describe the calling peer: its address (and {@link AddressType}) and whether the transport is
     * secured with TLS.
     *
     * @param call the native server call
     * @return the peer
     */
    public static Peer peer(ServerCall<byte[], byte[]> call) {
        SocketAddress remote = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        SSLSession session = call.getAttributes().get(Grpc.TRANSPORT_ATTR_SSL_SESSION);
        AuthContext auth = session != null ? new AuthContext("tls") : AuthContext.insecure();

        return new Peer(String.valueOf(remote), addressType(remote), auth);
    }

    private static AddressType addressType(SocketAddress remote) {
        if (remote instanceof InetSocketAddress inet && inet.getAddress() != null) {
            return inet.getAddress() instanceof Inet6Address ? AddressType.IPV6 : AddressType.IPV4;
        }

        return AddressType.UNKNOWN;
    }

    /**
     * Translate the current gRPC context deadline into a {@link Deadline}.
     *
     * @return the deadline, or {@code Deadline.none()} when the client set none
     */
    public static Deadline deadline() {
        io.grpc.Deadline grpcDeadline = Context.current().getDeadline();

        if (grpcDeadline == null) {
            return Deadline.none();
        }

        return Deadline.fromTimeout(
                Duration.ofNanos(grpcDeadline.timeRemaining(TimeUnit.NANOSECONDS)));
    }

    /**
     * Write a {@link ServiceResponseContract} to the wire: initial headers, each message (drained
     * through the per-step cancellation check), then the status (with rich details as {@code
     * grpc-status-details-bin} when present) and trailers.
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

        io.grpc.Metadata trailers = toGrpcMetadata(response.getTrailingMetadata());
        byte[] details = response.getStatus().getDetails();
        if (details != null) {
            io.grpc.Metadata.Key<byte[]> detailsKey =
                    io.grpc.Metadata.Key.of(
                            "grpc-status-details-bin", io.grpc.Metadata.BINARY_BYTE_MARSHALLER);
            // The Status's own details are authoritative: overwrite any handler-set trailer under
            // the same key rather than appending a second value the client would ignore.
            trailers.discardAll(detailsKey);
            trailers.put(detailsKey, details);
        }

        call.close(status, trailers);
    }

    /**
     * Translate framework metadata to gRPC metadata, carrying both ASCII and binary ({@code -bin})
     * keys.
     *
     * @param metadata the framework metadata
     * @return the gRPC metadata
     */
    public static io.grpc.Metadata toGrpcMetadata(MetadataContract metadata) {
        io.grpc.Metadata grpcMetadata = new io.grpc.Metadata();

        for (var entry : metadata) {
            String name = entry.getKey();
            if (metadata.isBinaryKey(name)) {
                io.grpc.Metadata.Key<byte[]> key =
                        io.grpc.Metadata.Key.of(name, io.grpc.Metadata.BINARY_BYTE_MARSHALLER);
                for (Object value : entry.getValue()) {
                    grpcMetadata.put(key, (byte[]) value);
                }
            } else {
                io.grpc.Metadata.Key<String> key =
                        io.grpc.Metadata.Key.of(name, io.grpc.Metadata.ASCII_STRING_MARSHALLER);
                for (Object value : entry.getValue()) {
                    grpcMetadata.put(key, String.valueOf(value));
                }
            }
        }

        return grpcMetadata;
    }

    /**
     * Translate inbound gRPC metadata to framework metadata, carrying both ASCII and binary ({@code
     * -bin}) keys so handlers can read auth/tracing headers.
     *
     * @param headers the inbound gRPC metadata
     * @return the framework metadata
     */
    public static MetadataContract fromGrpcMetadata(io.grpc.Metadata headers) {
        MetadataContract metadata = new Metadata();

        // A name returned by keys() always has values, so getAll is never null here.
        for (String name : headers.keys()) {
            if (name.endsWith(io.grpc.Metadata.BINARY_HEADER_SUFFIX)) {
                for (byte[] value :
                        headers.getAll(
                                io.grpc.Metadata.Key.of(
                                        name, io.grpc.Metadata.BINARY_BYTE_MARSHALLER))) {
                    metadata = metadata.withAdded(name, value);
                }
            } else {
                for (String value :
                        headers.getAll(
                                io.grpc.Metadata.Key.of(
                                        name, io.grpc.Metadata.ASCII_STRING_MARSHALLER))) {
                    metadata = metadata.withAdded(name, value);
                }
            }
        }

        return metadata;
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
