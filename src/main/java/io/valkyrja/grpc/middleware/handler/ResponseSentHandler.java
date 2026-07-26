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
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;

/**
 * Walks the {@code ResponseSent} chain after the response has been written to the wire. This stage
 * always runs — including on the cancellation fast-exit path — so it does not apply the
 * cancellation short-circuit.
 */
public class ResponseSentHandler extends Handler<ResponseSentMiddlewareContract>
        implements ResponseSentHandlerContract {

    @SafeVarargs
    public ResponseSentHandler(
            ContainerContract container,
            Class<? extends ResponseSentMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public void responseSent(ServiceCallContract call, ServiceResponseContract response) {
        Class<? extends ResponseSentMiddlewareContract> next = this.next;
        if (next != null) {
            getMiddleware(next).responseSent(call, response, this);
        }
    }
}
