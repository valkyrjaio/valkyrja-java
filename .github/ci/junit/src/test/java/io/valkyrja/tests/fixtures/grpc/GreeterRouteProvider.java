/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import java.util.List;

/** Fixture route provider supplying the {@link GreeterController} to the service map. */
public class GreeterRouteProvider implements GrpcRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of(GreeterController.class);
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
