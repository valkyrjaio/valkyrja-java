/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.request.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.request.factory.ServerFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ServerFactory}. */
final class ServerFactoryTest {

    @Test
    void normalizeServerReturnsServerWithAuthorization() {
        var server = Map.of("HTTP_AUTHORIZATION", "Bearer token");

        assertTrue(ServerFactory.normalizeServer(server).containsKey("HTTP_AUTHORIZATION"));
    }

    @Test
    void normalizeServerReturnsServerWithoutAuthorization() {
        var server = Map.of("SERVER_NAME", "host");

        assertTrue(ServerFactory.normalizeServer(server).containsKey("SERVER_NAME"));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new ServerFactory() {});
    }
}
