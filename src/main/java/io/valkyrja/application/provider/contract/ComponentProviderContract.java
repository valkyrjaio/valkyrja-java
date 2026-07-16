/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.provider.contract;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

public interface ComponentProviderContract {

    /**
     * Get the component providers this component depends on.
     *
     * @param app the application
     * @return list of component provider instances
     */
    List<ComponentProviderContract> getComponentProviders(ApplicationContract app);

    /**
     * Get the component's container service providers.
     *
     * @param app the application
     * @return list of container provider instances
     */
    List<ServiceProviderContract> getContainerProviders(ApplicationContract app);

    /**
     * Get the component's event listener providers.
     *
     * @param app the application
     * @return list of event provider instances
     */
    List<ListenerProviderContract> getEventProviders(ApplicationContract app);

    /**
     * Get the component's CLI route providers.
     *
     * @param app the application
     * @return list of CLI route provider instances
     */
    List<CliRouteProviderContract> getCliProviders(ApplicationContract app);

    /**
     * Get the component's HTTP route providers.
     *
     * @param app the application
     * @return list of HTTP route provider instances
     */
    List<HttpRouteProviderContract> getHttpProviders(ApplicationContract app);

    /**
     * Get the component's gRPC route providers.
     *
     * <p>Defaulted to an empty list: gRPC is an optional protocol, so only components that actually
     * contribute gRPC routes override this — every other component inherits the empty default
     * rather than declaring a redundant override.
     *
     * @param app the application
     * @return list of gRPC route provider instances
     */
    default List<GrpcRouteProviderContract> getGrpcProviders(ApplicationContract app) {
        return List.of();
    }
}
