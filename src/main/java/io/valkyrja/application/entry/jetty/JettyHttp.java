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
import io.valkyrja.http.message.request.factory.RequestFactory;
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
 * that request.
 */
public class JettyHttp extends WorkerHttp {

    /**
     * Start the Jetty server worker loop.
     *
     * @param config the HTTP configuration
     * @throws Exception if Jetty fails to start
     */
    public static void run(HttpConfigContract config) throws Exception {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        Server server = new Server(config.port());
        server.setHandler(
                new Handler.Abstract() {
                    @Override
                    public boolean handle(Request request, Response response, Callback callback) {
                        WorkerHttp.handle(app, data, getRequest(request, response));
                        callback.succeeded();
                        return true;
                    }
                });

        server.start();
        server.join();
    }

    /**
     * Get the HTTP request from a Jetty request/response pair.
     *
     * <p>Override to populate the request from Jetty metadata (headers, body, remote address, etc.)
     * once the full request adapter exists.
     *
     * @param request the incoming Jetty request
     * @param response the outgoing Jetty response
     * @return the current server request
     */
    public static ServerRequestContract getRequest(Request request, Response response) {
        return RequestFactory.fromGlobals();
    }
}
