/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.entry.exchange;

import com.sun.net.httpserver.HttpServer;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.Http;
import io.valkyrja.throwable.exception.RuntimeException;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * CGI-style HTTP entry point for the built-in Sun {@link HttpServer}.
 *
 * <p>Re-bootstraps the full application on every incoming exchange — clean container isolation per
 * request at the cost of full bootstrap overhead each time. For production use prefer {@link
 * ExchangeHttp}, which bootstraps once and isolates each request with a {@link
 * io.valkyrja.container.manager.ChildContainer}.
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
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }

        server.createContext("/", exchange -> Http.run(config));
        server.start();
    }
}
