/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.GrpcApplicationComponentProvider;
import io.valkyrja.application.provider.abstract_.ComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

/** Fixture component provider that pulls in the gRPC stack and registers the greeter routes. */
public class GreeterComponentProviderFixture extends ComponentProvider {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(new GrpcApplicationComponentProvider());
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders(ApplicationContract app) {
        return List.of(new GreeterServiceProviderFixture());
    }

    @Override
    public List<ListenerProviderContract> getEventProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<CliRouteProviderContract> getCliProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<HttpRouteProviderContract> getHttpProviders(ApplicationContract app) {
        return List.of();
    }

    @Override
    public List<GrpcRouteProviderContract> getGrpcProviders(ApplicationContract app) {
        return List.of(new GreeterRouteProviderFixture());
    }
}
