/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.entry.abstract_;

import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.message.stream.contract.OutboundStreamContract;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

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
 * between {@code SendingResponse} and {@code ResponseSent}, matching the wire order.
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
     * ResponseSent}.
     *
     * @param app the frozen parent application (returned by {@link #bootstrap})
     * @param data the container data snapshot captured after {@link #bootstrap}
     * @param call the inbound call
     * @param writer writes the response to the wire (invoked before {@code ResponseSent})
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

        try {
            writer.accept(response);
        } finally {
            // ResponseSent middleware must run even when the wire write blows up, so per-call
            // resources are released and observers still see the call complete.
            handler.terminate(call, response);
        }
    }

    /**
     * Handle a single streaming-model (bidirectional) call. Unlike {@link #dispatch}, the handler
     * is invoked immediately (not after half-close) and emits messages through the call's push sink
     * while it reads live inbound; the adapter runs this on a per-call virtual thread.
     *
     * <p>The pipeline still runs once per call: {@code SendingResponse} fires once at stream open
     * (the first emit, or the close when the handler emits nothing) against an OK shell whose
     * initial metadata becomes the response headers; the handler's returned terminal response
     * supplies the final status and trailing metadata; and {@code ResponseSent} fires once at
     * close.
     *
     * @param app the frozen parent application (returned by {@link #bootstrap})
     * @param data the container data snapshot captured after {@link #bootstrap}
     * @param callFactory builds the streaming {@link ServiceCallContract} around the supplied
     *     outbound sink (so the call carries its live inbound stream and push sink)
     * @param outbound the transport-side outbound stream
     */
    public static void dispatchStreaming(
            ApplicationContract app,
            ContainerData data,
            Function<Consumer<Object>, ServiceCallContract> callFactory,
            OutboundStreamContract outbound) {
        ContainerContract childContainer = getChildContainer(app, data);
        ApplicationContract childApp = getChildApplication(app, childContainer);

        bootstrapChildContainer(childApp, childContainer);

        ServiceHandlerContract handler = childContainer.getSingleton(ServiceHandlerContract.class);

        AtomicReference<ServiceCallContract> callRef = new AtomicReference<>();
        boolean[] opened = {false};

        ServiceCallContract call =
                callFactory.apply(
                        message -> {
                            // callRef is set before the handler (and thus any emit) runs.
                            openStream(
                                    handler,
                                    Objects.requireNonNull(callRef.get()),
                                    outbound,
                                    opened);
                            outbound.sendMessage(message);
                        });
        callRef.set(call);

        ServiceResponseContract terminal = handler.handle(call);

        // Open the stream once even if the handler emitted nothing, so SendingResponse always fires
        // before the close and the open/close pairing stays symmetric.
        openStream(handler, call, outbound, opened);

        try {
            outbound.close(terminal);
        } finally {
            handler.terminate(call, terminal);
        }
    }

    /**
     * Commit the stream's initial headers exactly once. {@code SendingResponse} governs the
     * headers; at open the final status is unknown, so it runs against an OK shell whose initial
     * metadata is sent as the response headers.
     */
    private static void openStream(
            ServiceHandlerContract handler,
            ServiceCallContract call,
            OutboundStreamContract outbound,
            boolean[] opened) {
        if (opened[0]) {
            return;
        }
        opened[0] = true;
        ServiceResponseContract shell = handler.sending(call, ServiceResponse.ok());
        outbound.sendHeaders(shell.getInitialMetadata());
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
