/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.server.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.dispatcher.contract.RouterContract;
import io.valkyrja.grpc.server.handler.ServiceHandler;
import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;
import java.util.Map;
import java.util.function.Consumer;

/** Publishes the gRPC {@code ServiceHandler}, wired to the shared stage-handler singletons. */
public class GrpcServerServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ServiceHandlerContract.class, GrpcServerServiceProvider::publishServiceHandler);
    }

    public static void publishServiceHandler(ContainerContract container) {
        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        container.setSingleton(
                ServiceHandlerContract.class,
                new ServiceHandler(
                        container,
                        container.getSingleton(RouterContract.class),
                        container.getSingleton(CallReceivedHandlerContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(SendingResponseHandlerContract.class),
                        container.getSingleton(ResponseSentHandlerContract.class),
                        app.getDebugMode()));
    }
}
