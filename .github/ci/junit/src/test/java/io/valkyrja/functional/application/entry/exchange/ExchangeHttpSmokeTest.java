/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.exchange;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.sun.net.httpserver.HttpServer;
import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.entry.exchange.ExchangeHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link ExchangeHttp} adapter over a real JDK {@link HttpServer}.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it starts a non-daemon server
 * that never returns); instead the adapter's request path — {@link ExchangeHttp#getRequest} feeding
 * {@link WorkerHttp#handle} — is driven through a real, stoppable server bound to an ephemeral port.
 */
@Timeout(20)
final class ExchangeHttpSmokeTest {

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

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    WorkerHttp.handle(app, data, ExchangeHttp.getRequest(exchange));
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                });
        server.start();

        try {
            send(server.getAddress().getPort(), dispatched);
        } finally {
            server.stop(0);
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
