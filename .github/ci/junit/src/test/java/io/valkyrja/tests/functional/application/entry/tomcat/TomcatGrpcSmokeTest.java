/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.functional.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.entry.tomcat.TomcatGrpc;
import io.valkyrja.tests.fixtures.application.entry.EntryConfigFixture;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link TomcatGrpc} adapter over a real embedded Tomcat (servlet) server.
 *
 * <p>Drives the adapter's own {@link TomcatGrpc#server} — the exact server the blocking {@code
 * run(...)} builds (grpc-servlet mounted on embedded Tomcat) — on an ephemeral port, confirms it
 * started, then stops it. This proves grpc-servlet-jakarta and tomcat-embed-core load and assemble
 * alongside the framework.
 */
@Timeout(20)
final class TomcatGrpcSmokeTest {

    @Test
    void serverBootsWithTheGrpcServletMounted() throws Exception {
        Tomcat tomcat = TomcatGrpc.server(EntryConfigFixture.grpcOnPort(0));

        try {
            assertTrue(tomcat.getServer().getState().isAvailable());
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
