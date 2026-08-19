/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.CliWithHttpApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ProcessExitingMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;
import java.util.function.Consumer;

public record CliConfig(
        String namespace,
        String dir,
        String version,
        String environment,
        boolean debugMode,
        String timezone,
        String key,
        String dataPath,
        String dataNamespace,
        String applicationName,
        String defaultCommandName,
        List<Class<? extends InputReceivedMiddlewareContract>> inputReceivedMiddleware,
        List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
        List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware,
        List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
        List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
        List<Class<? extends ProcessExitingMiddlewareContract>> processExitingMiddleware,
        List<ComponentProviderContract> providers,
        List<Consumer<ApplicationContract>> callbacks)
        implements CliConfigContract {

    // Compact constructor for defensive copying
    public CliConfig {
        inputReceivedMiddleware = List.copyOf(inputReceivedMiddleware);
        routeMatchedMiddleware = List.copyOf(routeMatchedMiddleware);
        routeNotMatchedMiddleware = List.copyOf(routeNotMatchedMiddleware);
        routeDispatchedMiddleware = List.copyOf(routeDispatchedMiddleware);
        throwableCaughtMiddleware = List.copyOf(throwableCaughtMiddleware);
        processExitingMiddleware = List.copyOf(processExitingMiddleware);
        providers = List.copyOf(providers);
        callbacks = List.copyOf(callbacks);
    }

    public CliConfig() {
        this(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/cli/provider/data",
                "app.cli.provider.data",
                "valkyrja",
                "list",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(new CliWithHttpApplicationComponentProvider()),
                List.of());
    }
}
