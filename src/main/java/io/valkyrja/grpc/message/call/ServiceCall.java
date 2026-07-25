/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.call;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.cancellation.contract.CancellationTokenContract;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.deadline.contract.DeadlineContract;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.peer.contract.PeerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Immutable {@link ServiceCallContract} implementation.
 *
 * <p>Built by the worker adapter from a native call and enriched with the resolved {@link
 * RouteContract} by the router via {@link #withRoute}. Messages are held as an agnostic {@link
 * Iterable} of {@link Object}.
 */
public class ServiceCall implements ServiceCallContract {

    protected final String method;
    protected final MetadataContract metadata;
    protected final DeadlineContract deadline;
    protected final CancellationTokenContract cancellation;
    protected final PeerContract peer;
    protected final Iterable<Object> messages;
    protected final @Nullable RouteContract route;

    public ServiceCall(String method, Iterable<Object> messages) {
        this(
                method,
                new Metadata(),
                Deadline.none(),
                CancellationToken.never(),
                Peer.insecure("unknown"),
                messages,
                null);
    }

    public ServiceCall(
            String method,
            MetadataContract metadata,
            DeadlineContract deadline,
            CancellationTokenContract cancellation,
            PeerContract peer,
            Iterable<Object> messages,
            @Nullable RouteContract route) {
        this.method = method;
        this.metadata = metadata;
        this.deadline = deadline;
        this.cancellation = cancellation;
        this.peer = peer;
        this.messages = messages;
        this.route = route;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public MetadataContract getMetadata() {
        return metadata;
    }

    @Override
    public DeadlineContract getDeadline() {
        return deadline;
    }

    @Override
    public CancellationTokenContract getCancellation() {
        return cancellation;
    }

    @Override
    public PeerContract getPeer() {
        return peer;
    }

    @Override
    public Iterable<Object> getMessages() {
        return messages;
    }

    @Override
    public @Nullable RouteContract getRoute() {
        return route;
    }

    @Override
    public boolean hasRoute() {
        return route != null;
    }

    @Override
    public ServiceCallContract withRoute(RouteContract route) {
        return new ServiceCall(method, metadata, deadline, cancellation, peer, messages, route);
    }

    @Override
    public <T> Iterable<T> cancellable(Iterable<T> source) {
        return () -> {
            Iterator<T> delegate = source.iterator();

            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    // Exit iteration early once the call is cancelled rather than throwing: the
                    // outbound drain then simply stops yielding and the call is closed normally,
                    // instead of a CancelledException escaping the transport listener. This mirrors
                    // the cooperative drain model in the architecture GRPC.md spec.
                    return !cancellation.isCancelled() && delegate.hasNext();
                }

                @Override
                public T next() {
                    return delegate.next();
                }
            };
        };
    }

    /**
     * Convenience factory for a unary call carrying a single inbound message.
     *
     * @param method the fully-qualified method
     * @param message the single inbound message
     * @return a new call
     */
    public static ServiceCall unary(String method, Object message) {
        return new ServiceCall(method, List.of(message));
    }
}
