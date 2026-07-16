/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.abstract_;

import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;
import java.util.function.Consumer;

/**
 * gRPC entry point for persistent worker runtimes (grpc-netty, servlet containers, etc.).
 *
 * <p>Usage — once at worker startup:
 *
 * <pre>{@code
 * ApplicationContract app  = WorkerGrpc.bootstrap(config);
 * ContainerData       data = (ContainerData) app.getContainer().getData();
 * }</pre>
 *
 * <p>Then per call inside the server's request loop:
 *
 * <pre>{@code
 * WorkerGrpc.dispatch(app, data, call, response -> writeToWire(response));
 * }</pre>
 *
 * <p>{@link #bootstrap} performs the full application bootstrap and force-resolves the service map
 * so it lives in the frozen parent container. {@link #dispatch} creates an isolated {@link
 * ChildContainer} per call so state never bleeds between calls; the adapter's {@code writer} runs
 * between {@code SendingResponse} and {@code Terminated}, matching the wire order.
 *
 * <p>All methods are {@code public static} so the lifecycle can be reproduced without extending
 * this class — useful for runtimes that already have their own class hierarchy.
 */
public abstract class WorkerGrpc extends App {

    /**
     * Bootstrap the application once at worker startup.
     *
     * @param config the gRPC configuration
     * @return the bootstrapped, frozen application
     */
    public static ApplicationContract bootstrap(GrpcConfigContract config) {
        ApplicationContract app = start(config);
        ContainerContract container = app.getContainer();

        bootstrapThrowableHandler(app, container);
        bootstrapParentServices(app);

        return app;
    }

    /**
     * Handle a single call using an isolated child container.
     *
     * <p>Resolves the {@link ServiceHandlerContract}, runs the pipeline through {@code
     * SendingResponse}, hands the response to {@code writer} to write to the wire, then runs {@code
     * Terminated}.
     *
     * @param app the frozen parent application (returned by {@link #bootstrap})
     * @param data the container data snapshot captured after {@link #bootstrap}
     * @param call the inbound call
     * @param writer writes the response to the wire (invoked before {@code Terminated})
     */
    public static void dispatch(
            ApplicationContract app,
            ContainerData data,
            ServiceCallContract call,
            Consumer<ServiceResponseContract> writer) {
        ContainerContract childContainer = getChildContainer(app, data);
        ApplicationContract childApp = getChildApplication(app, childContainer);

        bootstrapChildContainer(childApp, childContainer);

        ServiceHandlerContract handler = childContainer.getSingleton(ServiceHandlerContract.class);

        ServiceResponseContract response = handler.handle(call);
        response = handler.sending(call, response);

        writer.accept(response);

        handler.terminate(call, response);
    }

    /**
     * Get a child container scoped to the current call.
     *
     * @param app the frozen parent application
     * @param data the container data snapshot
     * @return the child container
     */
    public static ContainerContract getChildContainer(ApplicationContract app, ContainerData data) {
        return new ChildContainer(app.getContainer(), data);
    }

    /**
     * Get a child application scoped to the current call.
     *
     * @param app the frozen parent application
     * @param container the call-scoped child container
     * @return the child application for this call
     */
    public static ApplicationContract getChildApplication(
            ApplicationContract app, ContainerContract container) {
        return new ChildApplication(app, container);
    }

    /**
     * Bootstrap a child container with the call-scoped singletons.
     *
     * @param app the call-scoped child application
     * @param container the call-scoped child container
     */
    public static void bootstrapChildContainer(
            ApplicationContract app, ContainerContract container) {
        container.setSingleton(ApplicationContract.class, app);
        container.setSingleton(ContainerContract.class, container);
    }

    /**
     * Force-resolve the service map so it is cached in the frozen parent rather than rebuilt per
     * call.
     *
     * @param app the bootstrapped parent application
     */
    public static void bootstrapParentServices(ApplicationContract app) {
        app.getContainer().getSingleton(RouteCollectionContract.class);
    }
}
