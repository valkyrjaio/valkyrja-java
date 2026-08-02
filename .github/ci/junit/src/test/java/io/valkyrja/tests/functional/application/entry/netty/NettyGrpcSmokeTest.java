/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.functional.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.Server;
import io.valkyrja.application.entry.netty.NettyGrpc;
import io.valkyrja.tests.fixtures.application.entry.EntryConfigFixture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link NettyGrpc} adapter over a real grpc-netty transport.
 *
 * <p>Drives the adapter's own {@link NettyGrpc#server} — the exact server the blocking {@code
 * run(...)} builds — on an ephemeral port, confirms it bound, then shuts it down. The full
 * request/response round trip is covered by {@code functional/grpc/endtoend/GrpcNettyEndToEndTest}.
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
