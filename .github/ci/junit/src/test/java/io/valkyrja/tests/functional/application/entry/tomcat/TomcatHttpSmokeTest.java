/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.entry.tomcat.TomcatHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.tests.fixtures.application.entry.EntryConfigFixture;
import io.valkyrja.tests.fixtures.application.entry.HttpSmokeClient;
import io.valkyrja.tests.fixtures.application.entry.WorkerHttpProbe;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link TomcatHttp} adapter over a real embedded Tomcat server.
 *
 * <p>Drives the adapter's own {@link TomcatHttp#server} — the exact server the blocking {@code
 * run(...)} builds — on an ephemeral port, then confirms a full round trip: the adapter marshals
 * the incoming request (captured through the probe) and emits the framework response back through
 * the servlet response (the distinctive status and body are read off the socket). Because the
 * server is the adapter's own, the bound port is only non-zero if the adapter opened its connector,
 * so this also guards the connector wiring.
 */
@Timeout(20)
final class TomcatHttpSmokeTest {

    @Test
    void serverMarshalsTheRequestAndEmitsTheResponse() throws Exception {
        AtomicReference<ApplicationContract> appRef = new AtomicReference<>();

        Tomcat tomcat = TomcatHttp.server(EntryConfigFixture.httpOnPort(0, appRef::set));
        try {
            WorkerHttpProbe probe = WorkerHttpProbe.bind(appRef.get());
            int port = tomcat.getConnector().getLocalPort();
            assertTrue(port > 0, "the adapter should have opened a bound connector");

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
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
