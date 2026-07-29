/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.netty.channel.Channel;
import io.valkyrja.application.entry.netty.NettyHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.tests.fixtures.application.entry.EntryConfigFixture;
import io.valkyrja.tests.fixtures.application.entry.HttpSmokeClientFixture;
import io.valkyrja.tests.fixtures.application.entry.WorkerHttpProbeFixture;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link NettyHttp} adapter over a real Netty pipeline.
 *
 * <p>Drives the adapter's own {@link NettyHttp#server} — the exact pipeline the blocking {@code
 * run(...)} builds — on an ephemeral port, then confirms a full round trip: the adapter marshals
 * the incoming request (captured through the probe) and emits the framework response back through
 * the channel (the distinctive status and body are read off the socket). Closing the returned
 * channel shuts the event loops down.
 */
@Timeout(20)
final class NettyHttpSmokeTest {

    @Test
    void serverMarshalsTheRequestAndEmitsTheResponse() throws Exception {
        AtomicReference<ApplicationContract> appRef = new AtomicReference<>();

        Channel channel = NettyHttp.server(EntryConfigFixture.httpOnPort(0, appRef::set));
        try {
            WorkerHttpProbeFixture probe = WorkerHttpProbeFixture.bind(appRef.get());
            int port = ((InetSocketAddress) channel.localAddress()).getPort();

            String response = HttpSmokeClientFixture.get(port);

            assertTrue(response.startsWith("HTTP/"), response);
            assertTrue(response.contains(" " + WorkerHttpProbeFixture.STATUS + " "), response);
            assertTrue(response.contains(WorkerHttpProbeFixture.BODY), response);

            ServerRequestContract request = probe.capturedRequest();
            assertNotNull(request, "the adapter did not dispatch the incoming request");
            assertEquals(RequestMethod.GET, request.getMethod());
            assertEquals("/", request.getUri().getPath());
            assertEquals("1", request.getQueryParams().get("probe"));
        } finally {
            channel.close().sync();
        }
    }
}
