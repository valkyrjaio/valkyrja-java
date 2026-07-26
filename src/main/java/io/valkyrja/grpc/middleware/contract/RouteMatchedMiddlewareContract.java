/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** Middleware run after a route is matched, before the user handler. */
public interface RouteMatchedMiddlewareContract {

    RouteMatchedResult routeMatched(
            ServiceCallContract call, RouteContract route, RouteMatchedHandlerContract handler);
}
