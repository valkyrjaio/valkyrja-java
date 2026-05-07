/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry;

import com.sun.net.httpserver.HttpServer;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.throwable.exception.RuntimeException;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * CGI-style HTTP entry point for the built-in Sun {@link HttpServer}.
 *
 * <p>Re-bootstraps the full application on every incoming exchange — clean container
 * isolation per request at the cost of full bootstrap overhead each time. For
 * production use prefer {@link ExchangeHttp}, which bootstraps once and isolates
 * each request with a {@link io.valkyrja.container.manager.ChildContainer}.
 */
public class ExchangeCgiHttp extends Http {

    /**
     * Start the Sun HTTP server, re-bootstrapping the application on every request.
     *
     * @param config the HTTP configuration
     */
    public static void run(HttpConfigContract config) {
        HttpServer server = null;

        try {
            server = HttpServer.create(new InetSocketAddress(config.port()), 0);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        server.createContext("/", exchange -> Http.run(config));
        server.start();
    }
}
