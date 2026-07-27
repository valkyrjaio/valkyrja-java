/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.entry.jetty.JettyHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.fixtures.application.entry.EntryConfigFixture;
import io.valkyrja.fixtures.application.entry.HttpSmokeClient;
import io.valkyrja.fixtures.application.entry.WorkerHttpProbe;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link JettyHttp} adapter over a real embedded Jetty server.
 *
 * <p>Drives the adapter's own {@link JettyHttp#server} — the exact server the blocking {@code
 * run(...)} builds — on an ephemeral port, then confirms a full round trip: the adapter marshals the
 * incoming request (captured through the probe) and emits the framework response back through the
 * Jetty response (the distinctive status and body are read off the socket).
 */
@Timeout(20)
final class JettyHttpSmokeTest {

    @Test
    void serverMarshalsTheRequestAndEmitsTheResponse() throws Exception {
        AtomicReference<ApplicationContract> appRef = new AtomicReference<>();

        Server server = JettyHttp.server(EntryConfigFixture.httpOnPort(0, appRef::set));
        try {
            WorkerHttpProbe probe = WorkerHttpProbe.bind(appRef.get());
            int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();

            String response = HttpSmokeClient.get(port);

            assertTrue(response.startsWith("HTTP/"), response);
            assertTrue(response.contains(" " + WorkerHttpProbe.STATUS + " "), response);
            assertTrue(response.contains(WorkerHttpProbe.BODY), response);

            ServerRequestContract request = probe.capturedRequest();
            assertNotNull(request, "the adapter did not dispatch the incoming request");
            assertEquals(RequestMethod.GET, request.getMethod());
            assertEquals("/", request.getUri().getPath());
            assertEquals("1", request.getQueryParams().get("probe"));
        } finally {
            server.stop();
        }
    }
}
