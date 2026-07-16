/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
