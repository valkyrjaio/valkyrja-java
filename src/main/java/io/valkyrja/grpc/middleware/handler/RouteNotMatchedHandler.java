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
import io.valkyrja.grpc.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;

/**
 * Walks the {@code RouteNotMatched} chain with the two-question cancellation check bracketing each
 * step.
 */
public class RouteNotMatchedHandler extends Handler<RouteNotMatchedMiddlewareContract>
        implements RouteNotMatchedHandlerContract {

    @SafeVarargs
    public RouteNotMatchedHandler(
            ContainerContract container,
            Class<? extends RouteNotMatchedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ServiceResponseContract routeNotMatched(
            ServiceCallContract call, ServiceResponseContract response) {
        ServiceResponseContract preCheck = checkCancellation(call, response);
        if (preCheck != null) {
            return preCheck;
        }

        Class<? extends RouteNotMatchedMiddlewareContract> next = this.next;
        if (next == null) {
            return response;
        }

        ServiceResponseContract returned =
                getMiddleware(next).routeNotMatched(call, response, this);

        ServiceResponseContract postCheck = checkCancellation(call, returned);
        return postCheck != null ? postCheck : returned;
    }
}
