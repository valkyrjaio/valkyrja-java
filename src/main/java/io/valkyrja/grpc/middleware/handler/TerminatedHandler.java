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
import io.valkyrja.grpc.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;

/**
 * Walks the {@code Terminated} chain after the response has been written to the wire. This stage
 * always runs — including on the cancellation fast-exit path — so it does not apply the
 * cancellation short-circuit.
 */
public class TerminatedHandler extends Handler<TerminatedMiddlewareContract>
        implements TerminatedHandlerContract {

    @SafeVarargs
    public TerminatedHandler(
            ContainerContract container,
            Class<? extends TerminatedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public void terminated(ServiceCallContract call, ServiceResponseContract response) {
        Class<? extends TerminatedMiddlewareContract> next = this.next;
        if (next != null) {
            getMiddleware(next).terminated(call, response, this);
        }
    }
}
