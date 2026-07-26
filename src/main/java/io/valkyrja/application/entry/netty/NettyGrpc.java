/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.netty;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.entry.grpc.GrpcBridge;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * gRPC entry point for the grpc-netty worker runtime.
 *
 * <p>Bootstraps the application once, then serves every inbound call through {@link GrpcBridge},
 * which dispatches to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the
 * lifetime of that call.
 */
public class NettyGrpc extends WorkerGrpc {

    /**
     * Start the grpc-netty server worker loop.
     *
     * @param config the gRPC configuration
     * @throws IOException if the server fails to bind
     * @throws InterruptedException if the server thread is interrupted
     */
    public static void run(GrpcConfigContract config) throws IOException, InterruptedException {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        Server server =
                NettyServerBuilder.forPort(config.port())
                        .fallbackHandlerRegistry(GrpcBridge.registry(app, data))
                        .build()
                        .start();

        // Drain in-flight calls on JVM termination (SIGTERM / Ctrl-C) instead of dropping them.
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    server.shutdown();
                                    try {
                                        server.awaitTermination(30, TimeUnit.SECONDS);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }));

        server.awaitTermination();
    }
}
