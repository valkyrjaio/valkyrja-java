/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.abstract_;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;

/**
 * HTTP entry point for persistent worker runtimes (Sun HTTP, Netty, Tomcat, etc.).
 *
 * <p>Usage — once at worker startup:
 *
 * <pre>{@code
 * ApplicationContract app  = WorkerHttp.bootstrap(config);
 * ContainerData       data = (ContainerData) app.getContainer().getData();
 * }</pre>
 *
 * <p>Then per-request inside the worker loop:
 *
 * <pre>{@code
 * ServerRequestContract request = WorkerHttp.getRequest();
 * WorkerHttp.handle(app, data, request);
 * }</pre>
 *
 * <p>{@link #bootstrap} performs the full application bootstrap and force-resolves any services
 * that must live in the frozen parent container. {@link #handle} creates an isolated {@link
 * ChildContainer} per request so state never bleeds between requests.
 *
 * <p>Concrete subclasses add the server-specific request loop (e.g. registering a Sun HTTP handler,
 * attaching a Netty pipeline, etc.) and may override any of the decomposed methods to adapt to
 * their runtime context.
 *
 * <p>All methods are {@code public static} so the full bootstrap/handle lifecycle can be reproduced
 * without extending this class — useful for runtimes that cannot use inheritance (e.g. Go, or any
 * Java code that already has its own class hierarchy).
 */
public abstract class WorkerHttp extends App {

    /**
     * Bootstrap the application once at worker startup.
     *
     * <p>Call this once before the worker request loop begins. The returned {@link
     * ApplicationContract} is frozen after this call — its container must not be written to again.
     * Pass it (along with the snapshot from {@code app.getContainer().getData()}) to {@link
     * #handle} for every subsequent request.
     *
     * @param config the HTTP configuration
     * @return the bootstrapped, frozen application
     */
    public static ApplicationContract bootstrap(HttpConfigContract config) {
        ApplicationContract app = start(config);
        ContainerContract container = app.getContainer();

        bootstrapThrowableHandler(app, container);
        bootstrapParentServices(app);

        return app;
    }

    /**
     * Handle a single request using an isolated child container.
     *
     * <p>Creates a {@link ChildContainer} scoped to this request, bootstraps its request-scoped
     * singletons, dispatches the request, then discards the child. The parent application and its
     * container are never mutated.
     *
     * @param app the frozen parent application (returned by {@link #bootstrap})
     * @param data the container data snapshot captured after {@link #bootstrap}
     * @param request the current HTTP request
     */
    public static void handle(
            ApplicationContract app, ContainerData data, ServerRequestContract request) {
        ContainerContract childContainer = getChildContainer(app, data);
        ApplicationContract childApp = getChildApplication(app, childContainer);

        bootstrapChildContainer(childApp, childContainer);
        handleRequest(childContainer, request);
    }

    /**
     * Get a child container scoped to the current request.
     *
     * @param app the frozen parent application
     * @param data the container data snapshot
     * @return the child container
     */
    public static ContainerContract getChildContainer(ApplicationContract app, ContainerData data) {
        ContainerContract parent = app.getContainer();

        return new ChildContainer(parent, data);
    }

    /**
     * Get a child application scoped to the current request.
     *
     * <p>Returns a {@link ChildApplication} wrapping the frozen parent with the request-scoped
     * child container, so {@link ApplicationContract} resolves to the request-scoped wrapper rather
     * than the frozen parent.
     *
     * @param app the frozen parent application
     * @param container the request-scoped child container
     * @return the child application for this request
     */
    public static ApplicationContract getChildApplication(
            ApplicationContract app, ContainerContract container) {
        return new ChildApplication(app, container);
    }

    /**
     * Bootstrap a child container with the request-scoped singletons.
     *
     * @param app the request-scoped child application
     * @param container the request-scoped child container
     */
    public static void bootstrapChildContainer(
            ApplicationContract app, ContainerContract container) {
        container.setSingleton(ApplicationContract.class, app);
        container.setSingleton(ContainerContract.class, container);
    }

    /**
     * Dispatch the request via the {@link RequestHandlerContract} resolved from the container.
     *
     * @param container the request-scoped child container
     * @param request the current HTTP request
     */
    public static void handleRequest(ContainerContract container, ServerRequestContract request) {
        RequestHandlerContract handler = container.getSingleton(RequestHandlerContract.class);
        handler.run(request);
    }

    /**
     * Get the current HTTP request.
     *
     * <p>Override in subclasses to adapt request creation to the server runtime (e.g. extract from
     * a Netty {@code ChannelHandlerContext}).
     *
     * @return the current server request
     */
    public static ServerRequestContract getRequest() {
        return RequestFactory.fromGlobals();
    }

    /**
     * Force-resolve services that must be pre-built in the parent container.
     *
     * <p>Override in subclasses to eagerly resolve expensive shared services (e.g. the route
     * collection) so they are cached in the frozen parent rather than being re-created fresh on
     * every request's child container.
     *
     * @param app the bootstrapped parent application
     */
    public static void bootstrapParentServices(ApplicationContract app) {
        // Subclasses may force-resolve expensive shared services here, e.g.:
        // app.getContainer().getSingleton(CollectionContract.class);
    }
}
