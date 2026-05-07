/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.provider;

import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import java.util.List;

public class CliRoutingCliRouteProvider implements CliRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of();
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
