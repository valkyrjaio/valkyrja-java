/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.entry.netty.NettyGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link NettyGrpc} adapter over a real grpc-netty transport.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it blocks on {@code
 * awaitTermination()} and installs a JVM shutdown hook); instead the same server wiring — a
 * grpc-netty server backed by {@link GrpcBridge#registry} — is booted on an ephemeral port and
 * cleanly shut down. The full request/response round trip is covered by {@code
 * functional/grpc/endtoend/GrpcNettyEndToEndTest}.
 */
@Timeout(20)
final class NettyGrpcSmokeTest {

    @Test
    void serverBootsAndBindsWithTheBridgeRegistry() throws Exception {
        ApplicationContract app = WorkerGrpc.bootstrap(new GrpcConfig());
        ContainerData data = (ContainerData) app.getContainer().getData();

        Server server =
                NettyServerBuilder.forPort(0)
                        .fallbackHandlerRegistry(GrpcBridge.registry(app, data))
                        .build()
                        .start();

        try {
            assertFalse(server.isTerminated());
            assertTrue(server.getPort() > 0);
        } finally {
            server.shutdownNow();
            server.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
