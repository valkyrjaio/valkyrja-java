/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.entry.exchange;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.throwable.exception.RuntimeException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP entry point for the built-in Sun {@link HttpServer} worker runtime.
 *
 * <p>Uses the JDK's {@code com.sun.net.httpserver} package — no additional dependencies required.
 * Bootstraps once at startup, then dispatches each incoming exchange to an isolated {@link
 * io.valkyrja.container.manager.ChildContainer} so request state never bleeds between concurrent
 * exchanges, and writes the framework response back through the exchange.
 *
 * <p>For runtimes that require additional dependencies (Netty, Tomcat, Jetty, etc.) extend {@link
 * WorkerHttp} directly in a separate module.
 */
public class ExchangeHttp extends WorkerHttp {

    /**
     * Start the Sun HTTP server worker loop.
     *
     * <p>Bootstraps the application once, then registers a context handler that dispatches every
     * incoming exchange to an isolated child container for the lifetime of that request.
     *
     * @param config the HTTP configuration
     * @throws IOException if the server socket cannot be opened
     */
    public static void run(HttpConfigContract config) throws IOException {
        server(config);
    }

    /**
     * Create and start the Sun HTTP server, returning the running instance.
     *
     * <p>{@link #run} calls this and discards the handle (the server's own non-daemon threads keep
     * the JVM alive). Exposed separately so the server can be started, exercised, and stopped (e.g.
     * from a test) via {@link HttpServer#stop(int)}.
     *
     * @param config the HTTP configuration
     * @return the started Sun HTTP server
     * @throws IOException if the server socket cannot be opened
     */
    public static HttpServer server(HttpConfigContract config) throws IOException {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        HttpServer server = HttpServer.create(new InetSocketAddress(config.port()), 0);
        server.createContext(
                "/",
                exchange -> {
                    try {
                        dispatch(
                                app,
                                data,
                                getRequest(exchange),
                                response -> emit(response, exchange));
                    } finally {
                        exchange.close();
                    }
                });
        server.start();
        return server;
    }

    /**
     * Get the framework request from a Sun HTTP exchange.
     *
     * @param exchange the incoming Sun HTTP exchange
     * @return the current server request
     */
    public static ServerRequestContract getRequest(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String query = uri.getRawQuery();
        String requestUri = uri.getRawPath() + (query != null ? "?" + query : "");

        Map<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
            headers.put(header.getKey(), String.join(", ", header.getValue()));
        }

        String remoteAddr =
                exchange.getRemoteAddress() != null
                        ? exchange.getRemoteAddress().getAddress().getHostAddress()
                        : null;

        return request(
                exchange.getRequestMethod(),
                requestUri,
                query,
                exchange.getProtocol(),
                remoteAddr,
                headers,
                readBody(exchange));
    }

    /**
     * Write a framework response back out through the Sun HTTP exchange.
     *
     * @param response the framework response
     * @param exchange the Sun HTTP exchange to write through
     */
    public static void emit(ResponseContract response, HttpExchange exchange) {
        response.getHeaders()
                .getAll()
                .values()
                .forEach(
                        header ->
                                exchange.getResponseHeaders()
                                        .add(header.getName(), header.getHeaderLine()));

        byte[] body = response.getBody().getContents().getBytes(StandardCharsets.UTF_8);

        try {
            // A body length of -1 tells the Sun server there is no response body.
            exchange.sendResponseHeaders(
                    response.getStatusCode().getValue(), body.length == 0 ? -1 : body.length);

            if (body.length > 0) {
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }
    }

    /**
     * Read the raw request body from a Sun HTTP exchange.
     *
     * @param exchange the incoming Sun HTTP exchange
     * @return the raw request body
     */
    protected static String readBody(HttpExchange exchange) {
        try {
            return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }
    }
}
