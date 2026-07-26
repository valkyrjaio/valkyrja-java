/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.servlet.jakarta.ServletServerBuilder;
import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.entry.jetty.JettyGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GracefulHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link JettyGrpc} adapter over a real embedded Jetty (servlet) server.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it calls {@code server.join()}
 * and installs a JVM shutdown hook); instead the same wiring — a grpc-servlet backed by {@link
 * GrpcBridge#registry}, mounted on an embedded Jetty — is assembled and started on an ephemeral
 * port, then cleanly stopped. This proves grpc-servlet-jakarta and jetty-ee10-servlet load and
 * assemble alongside the framework.
 */
@Timeout(20)
final class JettyGrpcSmokeTest {

    @Test
    void serverBootsWithTheGrpcServletMounted() throws Exception {
        ApplicationContract app = WorkerGrpc.bootstrap(new GrpcConfig());
        ContainerData data = (ContainerData) app.getContainer().getData();

        ServletServerBuilder builder = new ServletServerBuilder();
        builder.fallbackHandlerRegistry(GrpcBridge.registry(app, data));
        Servlet grpcServlet = builder.buildServlet();

        Server server = new Server(0);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        ServletHolder holder = new ServletHolder(grpcServlet);
        holder.setAsyncSupported(true);
        context.addServlet(holder, "/*");
        GracefulHandler graceful = new GracefulHandler();
        graceful.setHandler(context);
        server.setHandler(graceful);
        server.start();

        try {
            assertTrue(server.isStarted());
        } finally {
            server.stop();
        }
    }
}
