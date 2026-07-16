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
     * Get the decoded inbound messages.
     *
     * @return the messages
     */
    Iterable<Object> getMessages();

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
     * Wrap a source iterable so iteration checks cancellation between items, throwing {@code
     * CancelledException} if the call is cancelled. A cooperation helper for user handlers.
     *
     * @param source the source iterable
     * @param <T> the item type
     * @return a cancellation-aware iterable
     */
    <T> Iterable<T> cancellable(Iterable<T> source);
}
