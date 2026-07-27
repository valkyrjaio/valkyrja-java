/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.tomcat;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.throwable.exception.RuntimeException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * HTTP entry point for the embedded Tomcat worker runtime.
 *
 * <p>Bootstraps the application once, then registers a servlet that dispatches every incoming
 * request to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the lifetime of
 * that request and writes the framework response back through the servlet response.
 */
public class TomcatHttp extends WorkerHttp {

    /**
     * Start the embedded Tomcat server worker loop.
     *
     * @param config the HTTP configuration
     * @throws LifecycleException if Tomcat fails to start
     */
    public static void run(HttpConfigContract config) throws LifecycleException {
        server(config).getServer().await();
    }

    /**
     * Build and start the embedded Tomcat server, returning the running instance without blocking.
     *
     * <p>{@link #run} calls this and then blocks on {@code getServer().await()}. Exposed separately
     * so the server can be started, exercised, and stopped (e.g. from a test) without the blocking
     * await.
     *
     * @param config the HTTP configuration
     * @return the started Tomcat server
     * @throws LifecycleException if Tomcat fails to start
     */
    public static Tomcat server(HttpConfigContract config) throws LifecycleException {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(config.port());
        // Force the default connector to be created; embedded Tomcat lazily creates it in
        // getConnector(), and without it start() brings up a server with no connector that listens
        // on nothing.
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", null);
        Tomcat.addServlet(
                ctx,
                "valkyrja",
                new HttpServlet() {
                    @Override
                    protected void service(HttpServletRequest req, HttpServletResponse resp) {
                        dispatch(app, data, getRequest(req), response -> emit(response, resp));
                    }
                });
        ctx.addServletMappingDecoded("/*", "valkyrja");

        tomcat.start();
        return tomcat;
    }

    /**
     * Get the framework request from a Tomcat servlet request.
     *
     * @param request the incoming servlet request
     * @return the current server request
     */
    public static ServerRequestContract getRequest(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String requestUri =
                queryString != null
                        ? request.getRequestURI() + "?" + queryString
                        : request.getRequestURI();

        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(
                    name, String.join(", ", java.util.Collections.list(request.getHeaders(name))));
        }

        return request(
                request.getMethod(),
                requestUri,
                queryString,
                request.getProtocol(),
                request.getRemoteAddr(),
                headers,
                readBody(request));
    }

    /**
     * Write a framework response back out through the Tomcat servlet response.
     *
     * @param response the framework response
     * @param servletResponse the servlet response to write through
     */
    public static void emit(ResponseContract response, HttpServletResponse servletResponse) {
        servletResponse.setStatus(response.getStatusCode().getValue());

        response.getHeaders()
                .getAll()
                .values()
                .forEach(
                        header ->
                                servletResponse.addHeader(
                                        header.getName(), header.getHeaderLine()));

        byte[] body = response.getBody().getContents().getBytes(StandardCharsets.UTF_8);

        try {
            servletResponse.getOutputStream().write(body);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }
    }

    /**
     * Read the raw request body from a Tomcat servlet request.
     *
     * @param request the incoming servlet request
     * @return the raw request body
     */
    protected static String readBody(HttpServletRequest request) {
        try {
            return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "", e);
        }
    }
}
