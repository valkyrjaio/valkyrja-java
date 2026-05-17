/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.abstract_;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.directory.Directory;
import io.valkyrja.application.kernel.Valkyrja;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.ServiceProvider;
import io.valkyrja.support.time.Microtime;

public abstract class App {

    private static Long APP_START = 0L;

    public static Long getAppStart() {
        return APP_START;
    }

    public static ApplicationContract start(ConfigContract config) {
        if (config.debugMode()) {
            defaultExceptionHandler();
        }

        appStart();
        directory(config.dir());

        return app(config);
    }

    public static void appStart() {
        APP_START = Microtime.get();
    }

    public static void directory(String dir) {
        Directory.setBasePath(dir);
    }

    public static ApplicationContract app(ConfigContract config) {
        ContainerContract container = getContainer();
        ApplicationContract app = getApplication(container, config);

        bootstrapServices(app, container, config);

        return app;
    }

    public static ContainerContract getContainer() {
        return new Container();
    }

    public static ApplicationContract getApplication(
            ContainerContract container, ConfigContract config) {
        return new Valkyrja(container, config);
    }

    public static void bootstrapServices(
            ApplicationContract app, ContainerContract container, ConfigContract config) {
        container.setSingleton(ApplicationContract.class, app);
        container.setSingleton(ConfigContract.class, config);

        if (config instanceof CliConfigContract cliConfig) {
            container.setSingleton(CliConfigContract.class, cliConfig);
        }

        if (config instanceof HttpConfigContract httpConfig) {
            container.setSingleton(HttpConfigContract.class, httpConfig);
        }

        app.publishProviderCallbacks();

        loadContainerData(container);
    }

    public static void loadContainerData(ContainerContract container) {
        if (!container.isSingleton(ContainerDataContract.class)) {
            publishContainerData(container);
        }

        ContainerDataContract containerData = container.getSingleton(ContainerDataContract.class);

        container.setFromData(containerData);
    }

    public static void publishContainerData(ContainerContract container) {
        ServiceProvider.publishData(container);
    }

    /**
     * Bootstrap the throwable handler for the application.
     *
     * <p>Override in subclasses to register a concrete error handler when debug mode is enabled.
     * No-op by default — Java does not ship a universal display-errors handler the way PHP ships
     * Whoops.
     */
    public static void bootstrapThrowableHandler(
            ApplicationContract app, ContainerContract container) {
        // Subclasses may register a debug-mode throwable handler here
    }

    /** Set a default exception handler used before the container-bound one is available. */
    public static void defaultExceptionHandler() {}
}
