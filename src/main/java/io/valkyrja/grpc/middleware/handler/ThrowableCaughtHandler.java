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
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;

/**
 * Walks the {@code ThrowableCaught} chain with the two-question cancellation check bracketing each
 * step.
 */
public class ThrowableCaughtHandler extends Handler<ThrowableCaughtMiddlewareContract>
        implements ThrowableCaughtHandlerContract {

    @SafeVarargs
    public ThrowableCaughtHandler(
            ContainerContract container,
            Class<? extends ThrowableCaughtMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ServiceResponseContract throwableCaught(
            ServiceCallContract call, ServiceResponseContract response, Throwable throwable) {
        ServiceResponseContract preCheck = checkCancellation(call, response);
        if (preCheck != null) {
            return preCheck;
        }

        Class<? extends ThrowableCaughtMiddlewareContract> next = this.next;
        if (next == null) {
            return response;
        }

        ServiceResponseContract returned =
                getMiddleware(next).throwableCaught(call, response, throwable, this);

        ServiceResponseContract postCheck = checkCancellation(call, returned);
        return postCheck != null ? postCheck : returned;
    }
}
