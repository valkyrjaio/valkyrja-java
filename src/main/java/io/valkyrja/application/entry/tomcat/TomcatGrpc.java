/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.entry.tomcat;

import io.grpc.servlet.jakarta.ServletServerBuilder;
import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

/**
 * gRPC entry point for the embedded Tomcat (servlet) worker runtime.
 *
 * <p>Bootstraps the application once, builds a grpc-servlet {@code GrpcServlet} backed by {@link
 * GrpcBridge}, and serves it from an embedded Tomcat over HTTP/2. Each inbound call dispatches to
 * an isolated {@link io.valkyrja.container.manager.ChildContainer}.
 */
public class TomcatGrpc extends WorkerGrpc {

    /**
     * Start the embedded Tomcat gRPC server worker loop.
     *
     * @param config the gRPC configuration
     * @throws LifecycleException if Tomcat fails to start
     */
    public static void run(GrpcConfigContract config) throws LifecycleException {
        Tomcat tomcat = server(config);

        // Stop the embedded server on JVM termination (SIGTERM / Ctrl-C): pause the connector to
        // stop accepting new connections, then stop and destroy the container.
        //
        // Unlike the Netty adapter, there is no true graceful drain here: grpc-servlet exposes no
        // io.grpc.Server to shut down (ServletServerBuilder.build() is internal-only and throws for
        // application callers), and the servlet's destroy() terminates the transport rather than
        // draining it. Pausing the connector first stops new calls; calls already in flight are not
        // guaranteed to complete before the container stops.
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    try {
                                        tomcat.getConnector().pause();
                                        tomcat.stop();
                                        tomcat.destroy();
                                    } catch (LifecycleException e) {
                                        // Best-effort shutdown; the JVM is exiting regardless.
                                    }
                                }));

        tomcat.getServer().await();
    }

    /**
     * Build and start the embedded Tomcat gRPC server, returning the running instance without
     * blocking or installing a shutdown hook.
     *
     * <p>{@link #run} calls this, adds the JVM shutdown hook, and blocks on {@code
     * getServer().await()}. Exposed separately so the server can be started, exercised, and stopped
     * (e.g. from a test) without the blocking await.
     *
     * @param config the gRPC configuration
     * @return the started Tomcat server
     * @throws LifecycleException if Tomcat fails to start
     */
    public static Tomcat server(GrpcConfigContract config) throws LifecycleException {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        ServletServerBuilder builder = new ServletServerBuilder();
        builder.fallbackHandlerRegistry(GrpcBridge.registry(app, data));
        Servlet grpcServlet = builder.buildServlet();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(config.port());
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", null);
        Wrapper wrapper = Tomcat.addServlet(ctx, "grpc", grpcServlet);
        wrapper.setAsyncSupported(true);
        ctx.addServletMappingDecoded("/*", "grpc");

        tomcat.start();
        return tomcat;
    }
}
