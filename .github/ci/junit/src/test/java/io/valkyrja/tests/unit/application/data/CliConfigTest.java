/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.application.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.CliConfig;
import io.valkyrja.application.provider.CliWithHttpApplicationComponentProvider;
import org.junit.jupiter.api.Test;

final class CliConfigTest {

    @Test
    void defaults() {
        var config = new CliConfig();

        assertEquals("App", config.namespace());
        assertEquals(System.getProperty("user.dir"), config.dir());
        assertEquals("1.0.0", config.version());
        assertEquals("production", config.environment());
        assertFalse(config.debugMode());
        assertEquals("UTC", config.timezone());
        assertEquals("secret_app_key", config.key());
        assertEquals("app/cli/provider/data", config.dataPath());
        assertEquals("app.cli.provider.data", config.dataNamespace());
        assertEquals("valkyrja", config.applicationName());
        assertEquals("list", config.defaultCommandName());
        assertTrue(config.inputReceivedMiddleware().isEmpty());
        assertTrue(config.routeMatchedMiddleware().isEmpty());
        assertTrue(config.routeNotMatchedMiddleware().isEmpty());
        assertTrue(config.routeDispatchedMiddleware().isEmpty());
        assertTrue(config.throwableCaughtMiddleware().isEmpty());
        assertTrue(config.processExitingMiddleware().isEmpty());
        assertFalse(config.providers().isEmpty());
        assertInstanceOf(CliWithHttpApplicationComponentProvider.class, config.providers().get(0));
        assertTrue(config.callbacks().isEmpty());
    }
}
