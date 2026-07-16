/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry;

import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.entry.abstract_.App;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;

/**
 * gRPC entry point for single-call use — bootstraps the application and dispatches one call.
 *
 * <p>Suitable for embedding or tests. For persistent server runtimes (grpc-netty, servlet
 * containers, etc.) use {@link io.valkyrja.application.entry.abstract_.WorkerGrpc} or one of its
 * concrete subclasses instead, which bootstraps once and reuses the frozen container per call.
 */
public class Grpc extends App {

    /**
     * Bootstrap the application from the given gRPC configuration.
     *
     * @param config the gRPC configuration
     * @return the bootstrapped application
     */
    public static ApplicationContract bootstrap(GrpcConfigContract config) {
        ApplicationContract app = start(config);
        bootstrapThrowableHandler(app, app.getContainer());

        return app;
    }

    /**
     * Bootstrap and handle a single call, returning the response after the full pipeline (including
     * {@code Terminated}).
     *
     * @param config the gRPC configuration
     * @param call the inbound call
     * @return the response
     */
    public static ServiceResponseContract handle(
            GrpcConfigContract config, ServiceCallContract call) {
        ApplicationContract app = bootstrap(config);
        ContainerContract container = app.getContainer();

        ServiceHandlerContract handler = container.getSingleton(ServiceHandlerContract.class);

        ServiceResponseContract response = handler.run(call);
        handler.terminate(call, response);

        return response;
    }
}
