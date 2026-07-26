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
import io.valkyrja.application.entry.netty.NettyGrpc;
import io.valkyrja.fixtures.application.entry.EntryConfigFixture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link NettyGrpc} adapter over a real grpc-netty transport.
 *
 * <p>Drives the adapter's own {@link NettyGrpc#server} — the exact server the blocking {@code
 * run(...)} builds — on an ephemeral port, confirms it bound, then shuts it down. The full
 * request/response round trip is covered by {@code
 * functional/grpc/endtoend/GrpcNettyEndToEndTest}.
 */
@Timeout(20)
final class NettyGrpcSmokeTest {

    @Test
    void serverBootsAndBindsWithTheBridgeRegistry() throws Exception {
        Server server = NettyGrpc.server(EntryConfigFixture.grpcOnPort(0));

        try {
            assertFalse(server.isTerminated());
            assertTrue(server.getPort() > 0, "the server should be listening on a bound port");
        } finally {
            server.shutdownNow();
            server.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
