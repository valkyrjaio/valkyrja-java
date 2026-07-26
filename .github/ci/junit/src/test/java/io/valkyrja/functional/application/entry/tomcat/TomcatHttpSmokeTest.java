/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.entry.tomcat.TomcatHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Smoke test for the {@link TomcatHttp} adapter over a real embedded Tomcat server.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it calls {@code
 * tomcat.getServer().await()} and never returns); instead the adapter's request path — {@link
 * TomcatHttp#getRequest} feeding {@link WorkerHttp#handle} — is driven through a real, stoppable
 * server bound to an ephemeral port.
 */
@Timeout(20)
final class TomcatHttpSmokeTest {

    @Test
    void serverDispatchesAnIncomingRequestThroughTheAdapter(@TempDir Path baseDir) throws Exception {
        ApplicationContract app = WorkerHttp.bootstrap(new HttpConfig());
        CountDownLatch dispatched = new CountDownLatch(1);
        RequestHandlerContract handler = mock(RequestHandlerContract.class);
        doAnswer(
                        invocation -> {
                            dispatched.countDown();
                            return null;
                        })
                .when(handler)
                .run(any());
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        ContainerData data = (ContainerData) app.getContainer().getData();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.toString());
        tomcat.setPort(0);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", baseDir.toString());
        Tomcat.addServlet(
                ctx,
                "valkyrja",
                new HttpServlet() {
                    @Override
                    protected void service(HttpServletRequest req, HttpServletResponse resp) {
                        WorkerHttp.handle(app, data, TomcatHttp.getRequest(req, resp));
                    }
                });
        ctx.addServletMappingDecoded("/*", "valkyrja");

        tomcat.start();

        try {
            send(tomcat.getConnector().getLocalPort(), dispatched);
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    private static void send(int port, CountDownLatch dispatched) throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", port)) {
            OutputStream out = socket.getOutputStream();
            out.write(
                    "GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
                            .getBytes(StandardCharsets.US_ASCII));
            out.flush();
            assertTrue(
                    dispatched.await(10, TimeUnit.SECONDS),
                    "the adapter did not dispatch the incoming request");
        }
    }
}
