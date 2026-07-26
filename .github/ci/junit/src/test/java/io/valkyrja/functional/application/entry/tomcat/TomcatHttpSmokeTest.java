/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.entry.tomcat.TomcatHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.fixtures.application.entry.EntryConfigFixture;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link TomcatHttp} adapter over a real embedded Tomcat server.
 *
 * <p>Drives the adapter's own {@link TomcatHttp#server} — the exact server the blocking {@code
 * run(...)} builds — on an ephemeral port, then confirms an incoming request reaches the request
 * handler. It asserts on the connector the adapter itself registered (read via {@code
 * findConnectors()}, never {@code getConnector()}, which would lazily create one) so a regression
 * where the adapter forgets to open a connector fails the test. The bootstrapped application is
 * captured through the config callback so an observable handler can be bound before the request is
 * sent.
 */
@Timeout(20)
final class TomcatHttpSmokeTest {

    @Test
    void serverDispatchesAnIncomingRequestThroughTheAdapter() throws Exception {
        AtomicReference<ApplicationContract> appRef = new AtomicReference<>();
        CountDownLatch dispatched = new CountDownLatch(1);

        Tomcat tomcat = TomcatHttp.server(EntryConfigFixture.httpOnPort(0, appRef::set));
        try {
            bindRecordingHandler(appRef.get(), dispatched);
            // Read the connector the adapter itself registered — do NOT call tomcat.getConnector(),
            // which would lazily create and start one on the running service and mask a regression
            // where the adapter forgot to open a connector.
            Connector[] connectors = tomcat.getService().findConnectors();
            assertTrue(connectors.length > 0, "the adapter should have opened a connector");
            int port = connectors[0].getLocalPort();
            assertTrue(port > 0, "the connector should be bound to a port");
            send(port, dispatched);
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    private static void bindRecordingHandler(ApplicationContract app, CountDownLatch dispatched) {
        assertNotNull(app, "the bootstrap callback should have captured the application");
        RequestHandlerContract handler = mock(RequestHandlerContract.class);
        doAnswer(
                        invocation -> {
                            dispatched.countDown();
                            return null;
                        })
                .when(handler)
                .run(any());
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
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
