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
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

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
 * WorkerHttp.dispatch(app, data, request, response -> emit(response, nativeResponse));
 * }</pre>
 *
 * <p>{@link #bootstrap} performs the full application bootstrap and force-resolves any services
 * that must live in the frozen parent container. {@link #dispatch} creates an isolated {@link
 * ChildContainer} per request so state never bleeds between requests.
 *
 * <p>Unlike the CGI {@link io.valkyrja.application.entry.Http} entry — whose {@code run()} pipeline
 * ends in {@link ResponseContract#send()}, writing the response through the ambient {@code
 * System.out} SAPI sink — a persistent worker must write the response back through the runtime's
 * own response object. {@link #dispatch} therefore splits the pipeline: it runs {@code handle} then
 * the {@code SendingResponse} middleware, hands the resulting response to the adapter's {@code
 * emitter} (which writes it to the native runtime response), and only then runs {@code terminate}
 * ({@code ResponseSent}) — mirroring {@link WorkerGrpc#dispatch} and the PHP OpenSwoole /
 * RoadRunner worker entries.
 *
 * <p>Concrete subclasses add the server-specific request loop (e.g. registering a Sun HTTP handler,
 * attaching a Netty pipeline, etc.), translate the native request via {@link #getRequest()} (see
 * the {@link #serverParams} / {@link #request} helpers), and emit the response through the native
 * runtime response.
 *
 * <p>All methods are {@code public static} so the full bootstrap/dispatch lifecycle can be
 * reproduced without extending this class — useful for runtimes that cannot use inheritance (e.g.
 * Go, or any Java code that already has its own class hierarchy).
 */
public abstract class WorkerHttp extends App {

    /**
     * Bootstrap the application once at worker startup.
     *
     * <p>Call this once before the worker request loop begins. The returned {@link
     * ApplicationContract} is frozen after this call — its container must not be written to again.
     * Pass it (along with the snapshot from {@code app.getContainer().getData()}) to {@link
     * #dispatch} for every subsequent request.
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
     * Handle a single request using an isolated child container and emit the response through the
     * runtime's own response object.
     *
     * <p>Creates a {@link ChildContainer} scoped to this request, bootstraps its request-scoped
     * singletons, runs {@code handle} then the {@code SendingResponse} middleware, hands the
     * resulting response to {@code emitter} to write to the native runtime response, then runs
     * {@code terminate} ({@code ResponseSent}). The parent application and its container are never
     * mutated.
     *
     * <p>{@code terminate} runs in a {@code finally} so {@code ResponseSent} fires even when the
     * emit fails, matching {@link WorkerGrpc#dispatch} and the CGI {@code run()} ordering (the
     * response is emitted before {@code ResponseSent}).
     *
     * @param app the frozen parent application (returned by {@link #bootstrap})
     * @param data the container data snapshot captured after {@link #bootstrap}
     * @param request the current HTTP request
     * @param emitter writes the response to the native runtime response (invoked before {@code
     *     terminate})
     */
    public static void dispatch(
            ApplicationContract app,
            ContainerData data,
            ServerRequestContract request,
            Consumer<ResponseContract> emitter) {
        ContainerContract childContainer = getChildContainer(app, data);
        ApplicationContract childApp = getChildApplication(app, childContainer);

        bootstrapChildContainer(childApp, childContainer);

        RequestHandlerContract handler = childContainer.getSingleton(RequestHandlerContract.class);
        ResponseContract response = handler.handle(request);

        SendingResponseHandlerContract sendingResponseHandler =
                childContainer.getSingleton(SendingResponseHandlerContract.class);
        response = sendingResponseHandler.sendingResponse(request, response);

        childContainer.setSingleton(ResponseContract.class, response);

        try {
            emitter.accept(response);
        } finally {
            // ResponseSent must run even when the native emit blows up, so per-request resources
            // are released and observers still see the request complete.
            handler.terminate(request, response);
        }
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
     * Get the current HTTP request.
     *
     * <p>Override in subclasses to adapt request creation to the server runtime (e.g. extract from
     * a Netty {@code ChannelHandlerContext}) — typically by marshaling the native request's method,
     * URI, protocol, headers, and body through {@link #request}.
     *
     * @return the current server request
     */
    public static ServerRequestContract getRequest() {
        return RequestFactory.fromGlobals();
    }

    /**
     * Build a framework server request from a native runtime request's parts.
     *
     * <p>Marshals the {@code $_SERVER}-style params via {@link #serverParams}, parses the query
     * string into query params, lets {@link RequestFactory#fromGlobals} derive the cookies from the
     * {@code Cookie} header, and attaches the raw body as the request stream — mirroring the PHP
     * OpenSwoole / RoadRunner {@code getRequestFrom…Request} translation.
     *
     * <p>Parsed form body and uploaded files are left empty: unlike the PHP Swoole runtime (which
     * pre-parses them), the raw servlet / socket runtimes expose only the raw body, which callers
     * read through {@link ServerRequestContract#getBody()}. Content-type-aware body parsing is a
     * separate concern.
     *
     * @param method the request method (e.g. {@code GET})
     * @param requestUri the request target (path, optionally with query string)
     * @param queryString the raw query string (without a leading {@code ?}), or {@code null}
     * @param protocol the server protocol (e.g. {@code HTTP/1.1})
     * @param remoteAddr the remote client address, or {@code null} if unknown
     * @param headers the request headers, keyed by header name
     * @param body the raw request body
     * @return the framework server request
     */
    public static ServerRequestContract request(
            String method,
            String requestUri,
            @Nullable String queryString,
            String protocol,
            @Nullable String remoteAddr,
            Map<String, String> headers,
            String body) {
        Map<String, String> server =
                serverParams(method, requestUri, queryString, protocol, remoteAddr, headers);

        ServerRequestContract request =
                RequestFactory.fromGlobals(server, parseQueryString(queryString), null, null, null);

        Stream stream = new Stream();
        stream.write(body);
        stream.rewind();

        return (ServerRequestContract) request.withBody(stream);
    }

    /**
     * Marshal a {@code $_SERVER}-style params map from a native runtime request's parts.
     *
     * <p>Folds each request header into the PHP {@code $_SERVER} conventions the framework expects:
     * {@code Content-Type} / {@code Content-Length} become {@code CONTENT_TYPE} / {@code
     * CONTENT_LENGTH}, and every other header becomes {@code HTTP_<UPPER_SNAKE_CASE>}. Mirrors the
     * PHP {@code getServerParamsFromSwooleRequest} marshaling.
     *
     * @param method the request method
     * @param requestUri the request target
     * @param queryString the raw query string, or {@code null}
     * @param protocol the server protocol
     * @param remoteAddr the remote client address, or {@code null}
     * @param headers the request headers, keyed by header name
     * @return the marshaled server params
     */
    public static Map<String, String> serverParams(
            String method,
            String requestUri,
            @Nullable String queryString,
            String protocol,
            @Nullable String remoteAddr,
            Map<String, String> headers) {
        Map<String, String> server = new LinkedHashMap<>();

        server.put("REQUEST_METHOD", method);
        server.put("REQUEST_URI", requestUri);
        server.put("SERVER_PROTOCOL", protocol);

        if (queryString != null && !queryString.isEmpty()) {
            server.put("QUERY_STRING", queryString);
        }

        if (remoteAddr != null) {
            server.put("REMOTE_ADDR", remoteAddr);
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            String normalizedName = header.getKey().toUpperCase(Locale.ROOT).replace('-', '_');

            if (normalizedName.equals("CONTENT_TYPE") || normalizedName.equals("CONTENT_LENGTH")) {
                server.put(normalizedName, header.getValue());

                continue;
            }

            server.put("HTTP_" + normalizedName, header.getValue());
        }

        return server;
    }

    /**
     * Parse a raw query string into a query-params map.
     *
     * <p>Splits on {@code &}, decodes each {@code key=value} pair (percent- and {@code +}-decoded
     * as {@code application/x-www-form-urlencoded}), and treats a pair with no {@code =} as an
     * empty value.
     *
     * @param queryString the raw query string (with or without a leading {@code ?}), or {@code
     *     null}
     * @return the parsed query params (empty when the query string is absent or blank)
     */
    public static Map<String, Object> parseQueryString(@Nullable String queryString) {
        Map<String, Object> query = new LinkedHashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return query;
        }

        if (queryString.charAt(0) == '?') {
            queryString = queryString.substring(1);
        }

        for (String pair : queryString.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }

            int equals = pair.indexOf('=');
            String key = equals >= 0 ? pair.substring(0, equals) : pair;
            String value = equals >= 0 ? pair.substring(equals + 1) : "";

            query.put(
                    URLDecoder.decode(key, StandardCharsets.UTF_8),
                    URLDecoder.decode(value, StandardCharsets.UTF_8));
        }

        return query;
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
