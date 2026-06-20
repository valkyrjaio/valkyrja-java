/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.http.routing;

import io.valkyrja.classes.http.middleware.RequestReceivedOnlyHttpMiddleware;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.attribute.Route;
import io.valkyrja.http.routing.attribute.route.Middleware;
import io.valkyrja.http.routing.data.contract.RouteContract;

/** A controller with no class-level {@code @Path}/{@code @Name} and an unrelated middleware. */
public final class PlainHttpController {

    @Route(path = "/plain", name = "plain")
    @Middleware(name = RequestReceivedOnlyHttpMiddleware.class)
    public ResponseContract plain(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }
}
