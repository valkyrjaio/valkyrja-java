/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;

import java.util.List;
import java.util.function.Consumer;

/**
 * Immutable application configuration.
 *
 * @param namespace the application namespace (e.g. {@code App})
 * @param dir the base directory path
 * @param version the application version string
 * @param environment the environment name (e.g. {@code production})
 * @param debugMode whether the application is running in debug mode
 * @param timezone the default timezone (e.g. {@code UTC})
 * @param key the application encryption key
 * @param dataPath the application data directory path
 * @param dataNamespace the application data namespace
 * @param providers registered component application provider instances
 * @param callbacks custom initialization callbacks invoked during bootstrapping
 */
public record Config(
        String namespace,
        String dir,
        String version,
        String environment,
        boolean debugMode,
        String timezone,
        String key,
        String dataPath,
        String dataNamespace,
        List<ComponentProviderContract> providers,
        List<Consumer<ApplicationContract>> callbacks)
        implements ConfigContract {

    // Compact constructor for defensive copying
    public Config {
        providers = List.copyOf(providers);
        callbacks = List.copyOf(callbacks);
    }

    public Config() {
        this(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/provider/data",
                "app.provider.data",
                List.of(new ApplicationComponentProvider()),
                List.of());
    }
}
