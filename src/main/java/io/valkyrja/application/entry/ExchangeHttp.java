/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * HTTP entry point for the built-in Sun {@link HttpServer} worker runtime.
 *
 * <p>Uses the JDK's {@code com.sun.net.httpserver} package — no additional
 * dependencies required. Bootstraps once at startup, then dispatches each
 * incoming exchange to an isolated {@link io.valkyrja.container.manager.ChildContainer}
 * so request state never bleeds between concurrent exchanges.
 *
 * <p>For runtimes that require additional dependencies (Netty, Tomcat, Jetty, etc.)
 * extend {@link WorkerHttp} directly in a separate module.
 */
public class ExchangeHttp extends WorkerHttp {

    /**
     * Start the Sun HTTP server worker loop.
     *
     * <p>Bootstraps the application once, then registers a context handler that
     * dispatches every incoming exchange to an isolated child container for the
     * lifetime of that request.
     *
     * @param config the HTTP configuration
     * @throws IOException if the server socket cannot be opened
     */
    public static void run(HttpConfigContract config) throws IOException {
        ApplicationContract app  = bootstrap(config);
        ContainerData       data = (ContainerData) app.getContainer().getData();

        HttpServer server = HttpServer.create(new InetSocketAddress(config.port()), 0);
        server.createContext("/", exchange -> handle(app, data, getRequest(exchange)));
        server.start();
    }

    /**
     * Get the HTTP request from a Sun HTTP exchange.
     *
     * <p>Override in subclasses to populate the request from exchange metadata
     * (headers, body, remote address, etc.) once the full request adapter exists.
     *
     * @param exchange the incoming Sun HTTP exchange
     * @return the current server request
     */
    public static ServerRequestContract getRequest(HttpExchange exchange) {
        return RequestFactory.fromGlobals();
    }
}
