/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.jetty;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.throwable.exception.RuntimeException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;

/**
 * HTTP entry point for the Jetty worker runtime.
 *
 * <p>Bootstraps the application once, then registers a handler that dispatches every incoming
 * request to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the lifetime of
 * that request and writes the framework response back through the Jetty response.
 */
public class JettyHttp extends WorkerHttp {

    /**
     * Start the Jetty server worker loop.
     *
     * @param config the HTTP configuration
     * @throws Exception if Jetty fails to start
     */
    public static void run(HttpConfigContract config) throws Exception {
        server(config).join();
    }

    /**
     * Build and start the Jetty server, returning the running instance without blocking.
     *
     * <p>{@link #run} calls this and then blocks on {@link Server#join()}. Exposed separately so
     * the server can be started, exercised, and stopped (e.g. from a test) without the blocking
     * join.
     *
     * @param config the HTTP configuration
     * @return the started Jetty server
     * @throws Exception if Jetty fails to start
     */
    public static Server server(HttpConfigContract config) throws Exception {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        Server server = new Server(config.port());
        server.setHandler(
                new Handler.Abstract() {
                    @Override
                    public boolean handle(Request request, Response response, Callback callback) {
                        dispatch(
                                app,
                                data,
                                getRequest(request),
                                frameworkResponse -> emit(frameworkResponse, response, callback));
                        return true;
                    }
                });

        server.start();
        return server;
    }

    /**
     * Get the framework request from a Jetty request.
     *
     * @param jettyRequest the incoming Jetty request
     * @return the current server request
     */
    public static ServerRequestContract getRequest(Request jettyRequest) {
        HttpURI uri = jettyRequest.getHttpURI();
        String requestUri = uri.getPathQuery() != null ? uri.getPathQuery() : uri.getPath();

        Map<String, String> headers = new LinkedHashMap<>();
        for (HttpField field : jettyRequest.getHeaders()) {
            headers.merge(
                    field.getName(), field.getValue(), (existing, next) -> existing + ", " + next);
        }

        SocketAddress remote = jettyRequest.getConnectionMetaData().getRemoteSocketAddress();
        String remoteAddr =
                remote instanceof InetSocketAddress inet
                        ? inet.getAddress().getHostAddress()
                        : null;

        return request(
                jettyRequest.getMethod(),
                requestUri,
                uri.getQuery(),
                jettyRequest.getConnectionMetaData().getProtocol(),
                remoteAddr,
                headers,
                readBody(jettyRequest));
    }

    /**
     * Write a framework response back out through the Jetty response.
     *
     * <p>The Jetty {@link Callback} is completed by {@link Response#write}, so the handler must not
     * complete it separately.
     *
     * @param response the framework response
     * @param jettyResponse the Jetty response to write through
     * @param callback the Jetty callback completed by the write
     */
    public static void emit(ResponseContract response, Response jettyResponse, Callback callback) {
        jettyResponse.setStatus(response.getStatusCode().getValue());

        response.getHeaders()
                .getAll()
                .values()
                .forEach(
                        header ->
                                jettyResponse
                                        .getHeaders()
                                        .add(header.getName(), header.getHeaderLine()));

        byte[] body = response.getBody().getContents().getBytes(StandardCharsets.UTF_8);

        jettyResponse.write(true, ByteBuffer.wrap(body), callback);
    }

    /**
     * Read the raw request body from a Jetty request.
     *
     * @param jettyRequest the incoming Jetty request
     * @return the raw request body
     */
    protected static String readBody(Request jettyRequest) {
        try {
            return new String(
                    Request.asInputStream(jettyRequest).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }
    }
}
