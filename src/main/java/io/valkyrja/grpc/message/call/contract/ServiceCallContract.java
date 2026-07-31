/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.call.contract;

import io.valkyrja.grpc.message.cancellation.contract.CancellationTokenContract;
import io.valkyrja.grpc.message.deadline.contract.DeadlineContract;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.contract.PeerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.throwable.exception.GrpcConcurrentSendException;
import io.valkyrja.grpc.throwable.exception.GrpcNonStreamingSendException;
import org.jspecify.annotations.Nullable;

/**
 * The immutable inbound side of the wire: what the worker adapter hands to the kernel.
 *
 * <p>Messages are typed agnostically as {@link Object} (a single-element iterable for unary and
 * server-streaming calls, a lazy iterable for client-streaming). The concrete message type is the
 * per-application generated protobuf type and is never referenced by the framework.
 */
public interface ServiceCallContract {

    /**
     * Get the fully-qualified method, {@code "/package.Service/Method"} — the service-map key.
     *
     * @return the method
     */
    String getMethod();

    /**
     * Get the inbound metadata (request headers).
     *
     * @return the metadata
     */
    MetadataContract getMetadata();

    /**
     * Get the call deadline. Never null; may be {@code Deadline.none()}.
     *
     * @return the deadline
     */
    DeadlineContract getDeadline();

    /**
     * Get the cancellation token. Never null; may be {@code CancellationToken.never()}.
     *
     * @return the cancellation token
     */
    CancellationTokenContract getCancellation();

    /**
     * Get the connection peer. Never null; auth may be {@code "insecure"}.
     *
     * @return the peer
     */
    PeerContract getPeer();

    /**
     * Get the decoded inbound messages. Under the buffered model this is the fixed list captured
     * before dispatch; under the streaming model it is a live stream whose iteration blocks until
     * each message arrives and ends when the client half-closes.
     *
     * <p>Under the streaming model the stream also ends on cancellation — half-close and cancel
     * both terminate iteration identically. A handler that needs to tell an orderly end from a
     * cancelled one inspects {@link #getCancellation()} after the loop.
     *
     * @return the messages
     */
    Iterable<Object> getMessages();

    /**
     * Whether this call was dispatched under the streaming model (a bidirectional method). When
     * true, {@link #getMessages} is a live inbound stream and {@link #send} pushes outbound
     * messages while the handler runs; when false (the buffered model) the handler instead returns
     * a single {@code ServiceResponse} carrying its messages.
     *
     * @return true if dispatched under the streaming model
     */
    boolean isStreaming();

    /**
     * Push one outbound message to the client from within the handler (streaming model only). Sends
     * are serialized; the framework fires {@code SendingResponse} middleware once, on the first
     * send (stream open). Not for buffered calls — those return their messages on the {@code
     * ServiceResponse} instead.
     *
     * <p>Must be called from a single thread. The transport is not thread-safe, so a concurrent
     * send is rejected fast (rather than silently corrupting the stream) — a handler that fans work
     * out to multiple threads must funnel its emissions back through one.
     *
     * @param message the outbound message
     * @throws GrpcNonStreamingSendException if this call is not streaming
     * @throws GrpcConcurrentSendException if a concurrent {@code send} is detected
     */
    void send(Object message);

    /**
     * Get the resolved route, or null if the call has not yet been routed (or no route matched).
     *
     * @return the route, or null
     */
    @Nullable RouteContract getRoute();

    /**
     * Whether a route has been resolved for this call.
     *
     * @return true if a route is set
     */
    boolean hasRoute();

    /**
     * Return a copy with the resolved route set.
     *
     * @param route the resolved route
     * @return a new call
     */
    ServiceCallContract withRoute(RouteContract route);

    /**
     * Wrap a source iterable so iteration checks cancellation between items, exiting iteration
     * early (yielding no further items) once the call is cancelled. A cooperation helper for user
     * handlers and the outbound drain: it stops yielding rather than throwing, so a cancelled
     * stream ends cleanly. Handlers that want to fail loudly instead can call {@code
     * getCancellation().throwIfCancelled()}.
     *
     * @param source the source iterable
     * @param <T> the item type
     * @return a cancellation-aware iterable
     */
    <T> Iterable<T> cancellable(Iterable<T> source);
}
