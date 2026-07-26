/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.entry.jetty.JettyHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link JettyHttp} adapter over a real embedded Jetty server.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it calls {@code server.join()}
 * and never returns); instead the adapter's request path — {@link JettyHttp#getRequest} feeding
 * {@link WorkerHttp#handle} — is driven through a real, stoppable server bound to an ephemeral port.
 */
@Timeout(20)
final class JettyHttpSmokeTest {

    @Test
    void serverDispatchesAnIncomingRequestThroughTheAdapter() throws Exception {
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

        Server server = new Server(0);
        server.setHandler(
                new Handler.Abstract() {
                    @Override
                    public boolean handle(Request request, Response response, Callback callback) {
                        WorkerHttp.handle(app, data, JettyHttp.getRequest(request, response));
                        callback.succeeded();
                        return true;
                    }
                });
        server.start();

        try {
            int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
            send(port, dispatched);
        } finally {
            server.stop();
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
