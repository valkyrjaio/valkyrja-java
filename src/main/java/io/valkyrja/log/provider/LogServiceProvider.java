/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.log.logger.FileLogger;
import io.valkyrja.log.logger.NullLogger;
import io.valkyrja.log.logger.contract.LoggerContract;
import java.util.Map;
import java.util.function.Consumer;

public class LogServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                LoggerContract.class, LogServiceProvider::publishLogger,
                FileLogger.class, LogServiceProvider::publishFileLogger,
                NullLogger.class, LogServiceProvider::publishNullLogger);
    }

    /**
     * Publish the logger service.
     *
     * <p>Defaults to the {@link FileLogger} — the zero-dependency default, mirroring the PHP port's
     * file-writing default. An application that wants a different backend binds its own {@link
     * LoggerContract} before this publisher runs.
     *
     * @param container the container
     */
    public static void publishLogger(ContainerContract container) {
        container.setSingleton(LoggerContract.class, container.getSingleton(FileLogger.class));
    }

    /**
     * Publish the file logger service.
     *
     * @param container the container
     */
    public static void publishFileLogger(ContainerContract container) {
        container.setSingleton(FileLogger.class, new FileLogger());
    }

    /**
     * Publish the null logger service.
     *
     * @param container the container
     */
    public static void publishNullLogger(ContainerContract container) {
        container.setSingleton(NullLogger.class, new NullLogger());
    }
}
