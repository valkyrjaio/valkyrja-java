/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.http.routing.provider;

import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

/** Test HTTP route provider with no controllers or routes. */
public final class HttpRouteProviderClass implements HttpRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of();
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
