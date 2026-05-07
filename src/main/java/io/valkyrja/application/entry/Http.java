/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.App;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;

/**
 * HTTP entry point for traditional CGI / single-request runtimes.
 *
 * <p>Bootstraps the application from scratch on every request — suitable for
 * conventional servlet containers or raw CGI where no persistent process state
 * survives between requests. For persistent worker runtimes (Sun HTTP, Netty,
 * Tomcat, Jetty, etc.) use {@link io.valkyrja.application.entry.abstract_.WorkerHttp}
 * or one of its concrete subclasses instead.
 */
public class Http extends App {

    /**
     * Bootstrap the application and handle a single HTTP request.
     *
     * @param config the HTTP configuration
     */
    public static void run(HttpConfigContract config) {
        ApplicationContract    app       = start(config);
        ContainerContract      container = app.getContainer();

        bootstrapThrowableHandler(app, container);

        RequestHandlerContract handler = container.getSingleton(RequestHandlerContract.class);
        ServerRequestContract  request = getRequest();
        handler.run(request);
    }

    /**
     * Get the current HTTP request from the global server state.
     *
     * @return the current server request
     */
    public static ServerRequestContract getRequest() {
        return RequestFactory.fromGlobals();
    }
}
