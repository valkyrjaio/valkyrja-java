/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.entry.jetty.JettyGrpc;
import io.valkyrja.tests.fixtures.application.entry.EntryConfigFixture;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link JettyGrpc} adapter over a real embedded Jetty (servlet) server.
 *
 * <p>Drives the adapter's own {@link JettyGrpc#server} — the exact server the blocking {@code
 * run(...)} builds (grpc-servlet mounted on embedded Jetty) — on an ephemeral port, confirms it
 * started, then stops it. This proves grpc-servlet-jakarta and jetty-ee10-servlet load and assemble
 * alongside the framework.
 */
@Timeout(20)
final class JettyGrpcSmokeTest {

    @Test
    void serverBootsWithTheGrpcServletMounted() throws Exception {
        Server server = JettyGrpc.server(EntryConfigFixture.grpcOnPort(0));

        try {
            assertTrue(server.isStarted());
        } finally {
            server.stop();
        }
    }
}
