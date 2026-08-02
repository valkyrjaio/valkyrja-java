/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
