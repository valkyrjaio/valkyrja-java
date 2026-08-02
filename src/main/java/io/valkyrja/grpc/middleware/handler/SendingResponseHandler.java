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
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;

/**
 * Walks the {@code SendingResponse} chain. This stage always runs — including on the cancellation
 * fast-exit path — so it does not apply the cancellation short-circuit.
 */
public class SendingResponseHandler extends Handler<SendingResponseMiddlewareContract>
        implements SendingResponseHandlerContract {

    @SafeVarargs
    public SendingResponseHandler(
            ContainerContract container,
            Class<? extends SendingResponseMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ServiceResponseContract sendingResponse(
            ServiceCallContract call, ServiceResponseContract response) {
        Class<? extends SendingResponseMiddlewareContract> next = this.next;
        return next != null ? getMiddleware(next).sendingResponse(call, response, this) : response;
    }
}
