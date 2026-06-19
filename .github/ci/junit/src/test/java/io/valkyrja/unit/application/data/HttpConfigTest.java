/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.provider.HttpApplicationComponentProvider;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import org.junit.jupiter.api.Test;

final class HttpConfigTest {

    @Test
    void defaults() {
        var config = new HttpConfig();

        assertEquals("App", config.namespace());
        assertEquals(System.getProperty("user.dir"), config.dir());
        assertEquals("1.0.0", config.version());
        assertEquals("production", config.environment());
        assertFalse(config.debugMode());
        assertEquals("UTC", config.timezone());
        assertEquals("secret_app_key", config.key());
        assertEquals("app/http/provider/data", config.dataPath());
        assertEquals("app.http.provider.data", config.dataNamespace());
        assertEquals(8080, config.port());
        assertFalse(config.providers().isEmpty());
        assertInstanceOf(HttpApplicationComponentProvider.class, config.providers().get(0));
        assertTrue(config.callbacks().isEmpty());
        assertTrue(config.requestReceivedMiddleware().isEmpty());
        assertTrue(config.routeMatchedMiddleware().isEmpty());
        assertTrue(config.routeNotMatchedMiddleware().isEmpty());
        assertTrue(config.routeDispatchedMiddleware().isEmpty());
        assertEquals(
                LogThrowableCaughtMiddleware.class, config.throwableCaughtMiddleware().get(0));
        assertTrue(config.sendingResponseMiddleware().isEmpty());
        assertTrue(config.terminatedMiddleware().isEmpty());
    }
}
