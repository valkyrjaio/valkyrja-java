/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.server.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;
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
                        container.getSingleton(TerminatedHandlerContract.class),
                        app.getDebugMode()));
    }
}
