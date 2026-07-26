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
import io.valkyrja.http.message.request.factory.RequestFactory;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * HTTP entry point for the embedded Tomcat worker runtime.
 *
 * <p>Bootstraps the application once, then registers a servlet that dispatches every incoming
 * request to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the lifetime of
 * that request.
 */
public class TomcatHttp extends WorkerHttp {

    /**
     * Start the embedded Tomcat server worker loop.
     *
     * @param config the HTTP configuration
     * @throws LifecycleException if Tomcat fails to start
     */
    public static void run(HttpConfigContract config) throws LifecycleException {
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
                        handle(app, data, getRequest(req, resp));
                    }
                });
        ctx.addServletMappingDecoded("/*", "valkyrja");

        tomcat.start();
        tomcat.getServer().await();
    }

    /**
     * Get the HTTP request from a Tomcat servlet request/response pair.
     *
     * <p>Override to populate the request from servlet metadata (headers, body, remote address,
     * etc.) once the full request adapter exists.
     *
     * @param request the incoming servlet request
     * @param response the outgoing servlet response
     * @return the current server request
     */
    public static ServerRequestContract getRequest(
            HttpServletRequest request, HttpServletResponse response) {
        return RequestFactory.fromGlobals();
    }
}
