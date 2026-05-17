/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.provider.contract;

import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.List;

public interface HttpRouteProviderContract {

    List<Class<?>> getControllerClasses();

    List<RouteContract> getRoutes();
}
