/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.server.adapter.contract;

import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;

/**
 * Bridges an external gRPC server implementation (grpc-java, grpc-go, {@code @grpc/grpc-js}, …) to
 * the framework's {@link ServiceHandlerContract}.
 *
 * <p>This interface is part of the worker-agnostic surface — portable across every language port —
 * even though implementations are per-worker. An adapter accepts native calls, builds a {@code
 * ServiceCall}, hands it to the {@code ServiceHandler}, and translates the returned {@code
 * ServiceResponse} back to the library's native response API. Adapter-specific configuration (TLS,
 * thread pools, port binding) lives on the implementation, not here.
 *
 * <h2>Threading</h2>
 *
 * <p>A user handler may block. It can perform I/O, wait on a lock, or hold the outbound drain while
 * a slow peer catches up, and the framework never interrupts it — cancellation is cooperative in
 * every language port.
 *
 * <p>The framework therefore runs each handler on its own execution unit, off whatever thread the
 * library used to deliver the call. In Java that unit is a per-call virtual thread, for both the
 * buffered and the streaming model. Two guarantees follow, and an adapter may rely on both:
 *
 * <ul>
 *   <li>Delivering a call to the framework returns promptly. A blocking handler cannot occupy the
 *       library's callback or event-loop thread, so an adapter is free to dispatch on a direct
 *       executor.
 *   <li>The library stays free to deliver later events for the same call — a cancellation, or a
 *       transport-writability signal — while the handler runs. Those events reach the framework
 *       rather than queueing behind the handler, which is what makes a cooperative cancellation
 *       check observable mid-flight.
 * </ul>
 *
 * <p>An adapter must still supply the values the framework cannot read from the worker. Anything
 * bound to the calling thread — the deadline and any other ambient call context — is captured when
 * the call arrives and carried on the {@code ServiceCall}, never read later from the handler's
 * thread.
 *
 * <p>Writes to one call are serialized on the handler's thread; the framework never writes to a
 * single call from two threads at once, so a native call object need not be thread-safe against
 * itself.
 */
public interface ServiceAdapterContract {

    /**
     * Begin accepting calls, dispatching each to the given handler.
     *
     * @param handler the kernel entry point
     */
    void start(ServiceHandlerContract handler);

    /** Gracefully stop accepting calls and shut down. */
    void stop();
}
