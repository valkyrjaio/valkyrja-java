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
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/**
 * Walks the {@code RouteMatched} chain with the two-question cancellation check bracketing each
 * step.
 */
public class RouteMatchedHandler extends Handler<RouteMatchedMiddlewareContract>
        implements RouteMatchedHandlerContract {

    @SafeVarargs
    public RouteMatchedHandler(
            ContainerContract container,
            Class<? extends RouteMatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public RouteMatchedResult routeMatched(ServiceCallContract call, RouteContract route) {
        ServiceResponseContract preCheck = checkCancellation(call, null);
        if (preCheck != null) {
            return new RouteMatchedResult(route, preCheck);
        }

        Class<? extends RouteMatchedMiddlewareContract> next = this.next;
        if (next == null) {
            return new RouteMatchedResult(route, null);
        }

        RouteMatchedResult result = getMiddleware(next).routeMatched(call, route, this);

        ServiceResponseContract postCheck = checkCancellation(call, result.response());
        if (postCheck != null) {
            return new RouteMatchedResult(result.route(), postCheck);
        }

        return result;
    }
}
