/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.jetty;

import io.grpc.servlet.jakarta.ServletServerBuilder;
import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GracefulHandler;

/**
 * gRPC entry point for the embedded Jetty (servlet) worker runtime.
 *
 * <p>Bootstraps the application once, builds a grpc-servlet {@code GrpcServlet} backed by {@link
 * GrpcBridge}, and serves it from an embedded Jetty over HTTP/2. Each inbound call dispatches to an
 * isolated {@link io.valkyrja.container.manager.ChildContainer}.
 */
public class JettyGrpc extends WorkerGrpc {

    /**
     * Start the embedded Jetty gRPC server worker loop.
     *
     * @param config the gRPC configuration
     * @throws Exception if Jetty fails to start
     */
    public static void run(GrpcConfigContract config) throws Exception {
        Server server = server(config);

        // Stop the embedded server on JVM termination (SIGTERM / Ctrl-C); stop() honors the stop
        // timeout set in server() for the graceful window.
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    try {
                                        server.stop();
                                    } catch (Exception e) {
                                        // Best-effort shutdown; the JVM is exiting regardless.
                                    }
                                }));

        server.join();
    }

    /**
     * Build and start the embedded Jetty gRPC server, returning the running instance without
     * blocking or installing a shutdown hook.
     *
     * <p>{@link #run} calls this, adds the JVM shutdown hook, and blocks on {@link Server#join()}.
     * Exposed separately so the server can be started, exercised, and stopped (e.g. from a test)
     * without the blocking join.
     *
     * @param config the gRPC configuration
     * @return the started Jetty server
     * @throws Exception if Jetty fails to start
     */
    public static Server server(GrpcConfigContract config) throws Exception {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        ServletServerBuilder builder = new ServletServerBuilder();
        builder.fallbackHandlerRegistry(GrpcBridge.registry(app, data));
        Servlet grpcServlet = builder.buildServlet();

        Server server = new Server(config.port());

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        ServletHolder holder = new ServletHolder(grpcServlet);
        holder.setAsyncSupported(true);
        context.addServlet(holder, "/*");

        // Wrap the context in a GracefulHandler so shutdown actually drains: it is the only handler
        // in Jetty 12's tree that implements Graceful (the context and connectors do not), and it
        // tracks in-flight requests — grpc-servlet's async dispatches included. Without it,
        // Server.doStop finds no Graceful bean and the stop timeout below waits on nothing.
        GracefulHandler graceful = new GracefulHandler();
        graceful.setHandler(context);
        server.setHandler(graceful);
        // On stop, Jetty waits up to this timeout for the GracefulHandler's tracked in-flight
        // requests to complete before closing the connectors, so calls that finish within the
        // window drain cleanly. A streaming call still in flight past the timeout is cut off — the
        // grpc-servlet transport exposes no io.grpc.Server to drain further (build() is internal).
        server.setStopTimeout(30_000L);

        server.start();
        return server;
    }
}
