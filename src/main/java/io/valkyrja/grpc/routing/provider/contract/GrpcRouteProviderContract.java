/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.provider.contract;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;

/**
 * Supplies gRPC service controllers and/or pre-built routes for the service map, mirroring HTTP's
 * {@code HttpRouteProviderContract} and CLI's {@code CliRouteProviderContract}.
 */
public interface GrpcRouteProviderContract {

    List<Class<?>> getControllerClasses();

    List<RouteContract> getRoutes();
}
