/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.handler;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/**
 * Walks the {@code RouteDispatched} chain with the two-question cancellation check bracketing each
 * step.
 */
public class RouteDispatchedHandler extends Handler<RouteDispatchedMiddlewareContract>
        implements RouteDispatchedHandlerContract {

    @SafeVarargs
    public RouteDispatchedHandler(
            ContainerContract container,
            Class<? extends RouteDispatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ServiceResponseContract routeDispatched(
            ServiceCallContract call, ServiceResponseContract response, RouteContract route) {
        ServiceResponseContract preCheck = checkCancellation(call, response);
        if (preCheck != null) {
            return preCheck;
        }

        Class<? extends RouteDispatchedMiddlewareContract> next = this.next;
        if (next == null) {
            return response;
        }

        ServiceResponseContract returned =
                getMiddleware(next).routeDispatched(call, response, route, this);

        ServiceResponseContract postCheck = checkCancellation(call, returned);
        return postCheck != null ? postCheck : returned;
    }
}
