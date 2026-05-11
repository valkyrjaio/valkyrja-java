/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.middleware.contract.ExitedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;
import java.util.function.Consumer;

/**
 * Immutable CLI application configuration.
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
 * @param applicationName the application name
 * @param defaultCommandName the default command name
 * @param inputReceivedMiddleware middleware run when input is received
 * @param routeMatchedMiddleware middleware run when a route is matched
 * @param routeNotMatchedMiddleware middleware run when no route is matched
 * @param routeDispatchedMiddleware middleware run after a route is dispatched
 * @param throwableCaughtMiddleware middleware run when a throwable is caught
 * @param exitedMiddleware middleware run when the application exits
 * @param providers registered component application providers
 * @param callbacks custom initialization callbacks invoked during bootstrapping
 */
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
        List<Class<? extends ExitedMiddlewareContract>> exitedMiddleware,
        List<Class<? extends ComponentProviderContract>> providers,
        List<Consumer<ApplicationContract>> callbacks)
        implements CliConfigContract {

    // Compact constructor for defensive copying
    public CliConfig {
        inputReceivedMiddleware = List.copyOf(inputReceivedMiddleware);
        routeMatchedMiddleware = List.copyOf(routeMatchedMiddleware);
        routeNotMatchedMiddleware = List.copyOf(routeNotMatchedMiddleware);
        routeDispatchedMiddleware = List.copyOf(routeDispatchedMiddleware);
        throwableCaughtMiddleware = List.copyOf(throwableCaughtMiddleware);
        exitedMiddleware = List.copyOf(exitedMiddleware);
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
                List.of(ApplicationComponentProvider.class),
                List.of());
    }
}
