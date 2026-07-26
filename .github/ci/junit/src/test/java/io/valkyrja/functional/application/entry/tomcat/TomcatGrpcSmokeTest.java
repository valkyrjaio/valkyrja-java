/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.servlet.jakarta.ServletServerBuilder;
import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.entry.tomcat.TomcatGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import jakarta.servlet.Servlet;
import java.nio.file.Path;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Smoke test for the {@link TomcatGrpc} adapter over a real embedded Tomcat (servlet) server.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it calls {@code
 * tomcat.getServer().await()} and installs a JVM shutdown hook); instead the same wiring — a
 * grpc-servlet backed by {@link GrpcBridge#registry}, mounted on an embedded Tomcat — is assembled
 * and started on an ephemeral port, then cleanly stopped. This proves grpc-servlet-jakarta and
 * tomcat-embed-core load and assemble alongside the framework.
 */
@Timeout(20)
final class TomcatGrpcSmokeTest {

    @Test
    void serverBootsWithTheGrpcServletMounted(@TempDir Path baseDir) throws Exception {
        ApplicationContract app = WorkerGrpc.bootstrap(new GrpcConfig());
        ContainerData data = (ContainerData) app.getContainer().getData();

        ServletServerBuilder builder = new ServletServerBuilder();
        builder.fallbackHandlerRegistry(GrpcBridge.registry(app, data));
        Servlet grpcServlet = builder.buildServlet();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.toString());
        tomcat.setPort(0);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", baseDir.toString());
        Wrapper wrapper = Tomcat.addServlet(ctx, "grpc", grpcServlet);
        wrapper.setAsyncSupported(true);
        ctx.addServletMappingDecoded("/*", "grpc");
        tomcat.start();

        try {
            assertTrue(tomcat.getServer().getState().isAvailable());
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
