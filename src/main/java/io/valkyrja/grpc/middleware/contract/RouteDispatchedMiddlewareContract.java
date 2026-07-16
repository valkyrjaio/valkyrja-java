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
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** Middleware run after the user handler produces a response. */
public interface RouteDispatchedMiddlewareContract {

    ServiceResponseContract routeDispatched(
            ServiceCallContract call,
            ServiceResponseContract response,
            RouteContract route,
            RouteDispatchedHandlerContract handler);
}
