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
import io.valkyrja.application.data.contract.GrpcConfigContract;
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
import io.valkyrja.grpc.message.stream.InboundMessageStream;
import io.valkyrja.grpc.message.stream.contract.OutboundStreamContract;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.net.ssl.SSLSession;
import org.jspecify.annotations.Nullable;

/**
 * The transport-agnostic grpc-java bridge: it turns any inbound gRPC method into a call on {@link
 * WorkerGrpc#dispatch}. Depends only on the {@code io.grpc.*} API (an optional/compileOnly
 * dependency of core), so it is shared verbatim by every grpc-java transport (Netty, servlet, …).
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
        // The buffered path caps and rejects; the streaming path uses this as a flow-control
        // high-water mark. Configurable via GrpcConfig.
        int maxInboundMessages =
                app.getContainer().getSingleton(GrpcConfigContract.class).maxInboundMessages();

        // A bidirectional method (both streaming flags) is dispatched under the streaming model;
        // everything else buffers and dispatches once on half-close.
        boolean streaming = isBidirectional(app, "/" + fullMethodName);

        return (call, headers) -> {
            // Created up front and shared with the listener so cancellation (which can arrive
            // before half-close) has something to fire.
            CancellationToken token = new CancellationToken();

            // Fire the token from context cancellation. Both models run the handler off the
            // serialized listener path — the buffered one on a per-call virtual thread, the
            // streaming one on its own — so onCancel and the context listener can both transition
            // the token while a handler is mid-flight, and a cooperative handler polling
            // throwIfCancelled() observes it. The two paths are redundant by design.
            wireCancellation(token, Context.current());

            if (streaming) {
                return streamingListener(
                        app, data, call, headers, fullMethodName, token, maxInboundMessages);
            }

            // Ask for one message at a time so the transport applies backpressure instead of the
            // client flooding the server.
            call.request(1);
            List<Object> messages = new ArrayList<>();
            boolean[] rejected = {false};
            OutboundFlowControl flowControl = new OutboundFlowControl(call);

            return new ServerCall.Listener<>() {
                @Override
                public void onMessage(byte[] message) {
                    // After the overflow path closes the call, ignore further delivery rather than
                    // re-closing (an IllegalStateException from the transport). Unreachable while
                    // flow control stops requesting post-reject, but mirrors the onHalfClose guard.
                    if (rejected[0]) {
                        return;
                    }

                    if (messages.size() >= maxInboundMessages) {
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

                    // Built here, on the transport thread, because it reads the deadline from the
                    // gRPC Context, which is bound to this thread and not to the worker.
                    ServiceCallContract serviceCall =
                            buildCall(call, headers, fullMethodName, messages, token);

                    // Run the pipeline off the callback path. Returning immediately keeps a
                    // blocking handler out of the adapter's executor, so the adapter is free to
                    // use a direct executor, and leaves the listener able to deliver onReady and
                    // onCancel while the handler runs.
                    Thread.ofVirtual()
                            .name("grpc-call-" + fullMethodName)
                            .start(
                                    () ->
                                            WorkerGrpc.dispatch(
                                                    app,
                                                    data,
                                                    serviceCall,
                                                    response ->
                                                            write(
                                                                    call,
                                                                    serviceCall,
                                                                    response,
                                                                    flowControl)));
                }

                @Override
                public void onReady() {
                    flowControl.signal();
                }

                @Override
                public void onCancel() {
                    token.cancel(cancellationReason(Context.current().getDeadline()));
                    // Release a writer held on a transport that will never be ready again.
                    flowControl.signal();
                }
            };
        };
    }

    /** Whether the method's route is bidirectional (both streaming flags set). */
    private static boolean isBidirectional(ApplicationContract app, String method) {
        RouteCollectionContract collection =
                app.getContainer().getSingleton(RouteCollectionContract.class);
        if (!collection.has(method)) {
            return false;
        }
        RouteContract route = collection.get(method);
        return route.isClientStreaming() && route.isServerStreaming();
    }

    /**
     * Build the listener for a bidirectional (streaming-model) call. The handler is dispatched
     * immediately on a per-call virtual thread and reads a live inbound stream fed by the transport
     * while it pushes outbound messages; the deadline, peer, and headers are captured here (on the
     * transport thread, where the gRPC {@link Context} is bound) rather than on the virtual thread.
     */
    private static ServerCall.Listener<byte[]> streamingListener(
            ApplicationContract app,
            ContainerData data,
            ServerCall<byte[], byte[]> call,
            io.grpc.Metadata headers,
            String fullMethodName,
            CancellationToken token,
            int maxInboundMessages) {
        // Captured on the transport thread — Context-derived values must not be read on the worker.
        MetadataContract metadata = fromGrpcMetadata(headers);
        Deadline deadline = deadline();
        Peer peer = peer(call);

        // High-water flow control: allow up to the cap in flight, refilling one as the handler
        // drains each message so the queue never outgrows the bound.
        InboundMessageStream inbound = new InboundMessageStream(() -> call.request(1));
        call.request(maxInboundMessages);

        // The outbound counterpart: the push sink holds the handler's virtual thread while the
        // transport's send queue is full, so emitting cannot outrun a peer that stopped reading.
        OutboundFlowControl flowControl = new OutboundFlowControl(call);
        OutboundStreamContract outbound = new ServerCallOutboundStream(call, flowControl);

        Thread.ofVirtual()
                .name("grpc-stream-" + fullMethodName)
                .start(
                        () ->
                                WorkerGrpc.dispatchStreaming(
                                        app,
                                        data,
                                        sink ->
                                                new ServiceCall(
                                                        "/" + fullMethodName,
                                                        metadata,
                                                        deadline,
                                                        token,
                                                        peer,
                                                        inbound,
                                                        null,
                                                        sink),
                                        outbound));

        return new ServerCall.Listener<>() {
            @Override
            public void onMessage(byte[] message) {
                inbound.offer(message);
            }

            @Override
            public void onHalfClose() {
                inbound.complete();
            }

            @Override
            public void onReady() {
                flowControl.signal();
            }

            @Override
            public void onCancel() {
                token.cancel(cancellationReason(Context.current().getDeadline()));
                // Unblock the handler's read loop so the worker thread finishes and closes.
                inbound.complete();
                // Release a writer held on a transport that will never be ready again.
                flowControl.signal();
            }
        };
    }

    /** {@link OutboundStreamContract} backed by a gRPC {@link ServerCall}. */
    private static final class ServerCallOutboundStream implements OutboundStreamContract {

        private final ServerCall<byte[], byte[]> call;
        private final OutboundFlowControl flowControl;

        ServerCallOutboundStream(ServerCall<byte[], byte[]> call, OutboundFlowControl flowControl) {
            this.call = call;
            this.flowControl = flowControl;
        }

        @Override
        public void sendHeaders(MetadataContract initialMetadata) {
            call.sendHeaders(toGrpcMetadata(initialMetadata));
        }

        @Override
        public void sendMessage(Object message) {
            // Hold the emitting thread until the transport can take the message. A dead call
            // yields nothing to write to, so drop the emit rather than write into a closed stream
            // — the same shape as the buffered drain, which stops yielding once cancelled.
            if (!flowControl.awaitWritable()) {
                return;
            }

            call.sendMessage((byte[]) message);
        }

        @Override
        public void close(ServiceResponseContract terminal) {
            sendStatus(call, terminal);
        }
    }

    /**
     * Outbound flow control for one call: holds the writing thread while the transport's send queue
     * sits above its high-water mark, so a handler cannot outrun a peer that has stopped reading.
     * {@code maxInboundMessages} bounds the inbound side; this is its outbound counterpart.
     *
     * <p>Both models write from a per-call virtual thread rather than from the serialized listener
     * path, so {@link #signal} — driven by {@code onReady} — is the wakeup in both. The wait is
     * still bounded per pass and the loop re-reads {@link ServerCall#isReady()} itself, which is
     * the transport's own answer: readiness never depends on a callback arriving, so a signal that
     * races the park costs one interval instead of stranding the writer.
     */
    private static final class OutboundFlowControl {

        /** How long a held writer parks before re-reading the transport state itself. */
        private static final long POLL_NANOS = 10_000_000L;

        private final ServerCall<byte[], byte[]> call;

        /** The parked writer, or null when none is held. Written only by the writer itself. */
        private final AtomicReference<@Nullable Thread> waiter = new AtomicReference<>();

        OutboundFlowControl(ServerCall<byte[], byte[]> call) {
            this.call = call;
        }

        /**
         * Release a held writer: the transport can take more, or the call has ended. Unparking a
         * null thread is a no-op, so this needs no guard for the common case of no writer held.
         */
        void signal() {
            LockSupport.unpark(waiter.get());
        }

        /**
         * Hold the calling thread until the transport can accept another message.
         *
         * <p>A not-ready transport is a pause, not a cancellation — this waits however long the
         * peer takes and still reports true. It reports false only when the call ends underneath
         * the writer, so the drain stops instead of waiting on a transport that will never be ready
         * again.
         *
         * <p>Each park is bounded, so a signal that races the park costs one interval rather than
         * stranding the writer: the loop re-reads the transport regardless of why the park ended.
         *
         * @return true once the transport is writable; false when the call was cancelled, or the
         *     held thread was interrupted
         */
        boolean awaitWritable() {
            while (!call.isReady()) {
                if (call.isCancelled()) {
                    return false;
                }

                waiter.set(Thread.currentThread());
                LockSupport.parkNanos(POLL_NANOS);
                waiter.set(null);

                // parkNanos returns immediately on interrupt and leaves the flag set, so the
                // caller's interrupt status is preserved without re-asserting it here.
                if (Thread.currentThread().isInterrupted()) {
                    return false;
                }
            }

            return true;
        }
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
     * through the per-step cancellation check and the outbound flow-control gate), then the status
     * (with rich details as {@code grpc-status-details-bin} when present) and trailers.
     *
     * @param call the native server call
     * @param serviceCall the inbound call (for the cancellation check)
     * @param response the response to write
     */
    public static void write(
            ServerCall<byte[], byte[]> call,
            ServiceCallContract serviceCall,
            ServiceResponseContract response) {
        write(call, serviceCall, response, new OutboundFlowControl(call));
    }

    /**
     * Write a response, holding the drain on the given flow-control gate between messages so a
     * server-streaming response cannot outrun a peer that has stopped reading.
     *
     * @param call the native server call
     * @param serviceCall the inbound call (for the cancellation check)
     * @param response the response to write
     * @param flowControl the outbound gate, shared with the call's listener
     */
    private static void write(
            ServerCall<byte[], byte[]> call,
            ServiceCallContract serviceCall,
            ServiceResponseContract response,
            OutboundFlowControl flowControl) {
        call.sendHeaders(toGrpcMetadata(response.getInitialMetadata()));

        for (Object message : serviceCall.cancellable(response.getMessages())) {
            // The gate pauses; it never cancels. It reports false only once the call itself is
            // gone, at which point there is nothing left to drain into.
            if (!flowControl.awaitWritable()) {
                break;
            }

            call.sendMessage((byte[]) message);
        }

        sendStatus(call, response);
    }

    /**
     * Close the call with a response's status and trailing metadata, attaching rich details as
     * {@code grpc-status-details-bin} when present. Shared by the buffered {@link #write} and the
     * streaming {@link OutboundStreamContract#close}.
     *
     * @param call the native server call
     * @param response the response carrying the terminal status and trailing metadata
     */
    public static void sendStatus(
            ServerCall<byte[], byte[]> call, ServiceResponseContract response) {
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
