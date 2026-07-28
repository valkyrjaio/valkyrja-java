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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.Config;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

final class ConfigTest {

    @Test
    void defaults() {
        var config = new Config();

        assertEquals("App", config.namespace());
        assertEquals("1.0.0", config.version());
        assertEquals("production", config.environment());
        assertFalse(config.debugMode());
        assertEquals("UTC", config.timezone());
        assertEquals("secret_app_key", config.key());
        assertEquals("app/provider/data", config.dataPath());
        assertEquals("app.provider.data", config.dataNamespace());
        assertEquals(System.getProperty("user.dir"), config.dir());
        assertFalse(config.providers().isEmpty());
        assertInstanceOf(ApplicationComponentProvider.class, config.providers().get(0));
        assertTrue(config.callbacks().isEmpty());
    }

    @Test
    void canonicalConstructorDefensivelyCopiesLists() {
        var providers = new java.util.ArrayList<ComponentProviderContract>();
        providers.add(new ApplicationComponentProvider());
        var callbacks = new java.util.ArrayList<Consumer<ApplicationContract>>();
        callbacks.add(application -> {});

        var config =
                new Config(
                        "Custom",
                        "/tmp/app",
                        "9.9.9",
                        "testing",
                        true,
                        "America/Denver",
                        "key",
                        "data/path",
                        "data.path",
                        providers,
                        callbacks);

        // Mutating the source lists must not affect the stored copies.
        providers.clear();
        callbacks.clear();

        assertEquals("Custom", config.namespace());
        assertEquals("/tmp/app", config.dir());
        assertEquals("9.9.9", config.version());
        assertEquals("testing", config.environment());
        assertTrue(config.debugMode());
        assertEquals("America/Denver", config.timezone());
        assertEquals("key", config.key());
        assertEquals("data/path", config.dataPath());
        assertEquals("data.path", config.dataNamespace());
        assertEquals(1, config.providers().size());
        assertEquals(1, config.callbacks().size());
    }

    @Test
    void recordEqualityAndHashing() {
        var first = new Config();
        var second = new Config();

        assertEquals(first.namespace(), second.namespace());
        assertSame(first.getClass(), second.getClass());
    }
}
