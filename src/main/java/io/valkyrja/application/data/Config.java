/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import java.util.List;
import java.util.function.Consumer;

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
