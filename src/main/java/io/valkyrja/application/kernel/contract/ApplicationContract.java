/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.kernel.contract;

import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

public interface ApplicationContract {

    /**
     * Get the dependency injection container.
     *
     * @return the container
     */
    ContainerContract getContainer();

    /** Invoke all registered component provider callbacks on this application. */
    void publishProviderCallbacks();

    /**
     * Get all component-level application providers.
     *
     * @return list of application providers
     */
    List<Class<? extends ComponentProviderContract>> getProviders();

    /**
     * Aggregate all container service providers from all registered components.
     *
     * @return flat list of container providers
     */
    List<Class<? extends ServiceProviderContract>> getContainerProviders();

    /**
     * Aggregate all event listener providers from all registered components.
     *
     * @return flat list of event providers
     */
    List<Class<? extends ListenerProviderContract>> getEventProviders();

    /**
     * Aggregate all CLI route providers from all registered components.
     *
     * @return flat list of CLI providers
     */
    List<Class<? extends CliRouteProviderContract>> getCliProviders();

    /**
     * Aggregate all HTTP route providers from all registered components.
     *
     * @return flat list of HTTP providers
     */
    List<Class<? extends HttpRouteProviderContract>> getHttpProviders();

    /**
     * Whether the application is running in debug mode.
     *
     * @return true if debug mode is enabled
     */
    boolean getDebugMode();

    /**
     * Get the environment name (e.g. {@code production}, {@code local}, {@code testing}).
     *
     * @return the environment string
     */
    String getEnvironment();

    /**
     * Get the application version string.
     *
     * @return the version
     */
    String getVersion();
}
