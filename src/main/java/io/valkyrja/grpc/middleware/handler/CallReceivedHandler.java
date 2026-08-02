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
import io.valkyrja.grpc.middleware.contract.CallReceivedMiddlewareContract;
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.abstract_.Handler;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;

/**
 * Walks the {@code CallReceived} chain with the two-question cancellation check bracketing each
 * step.
 */
public class CallReceivedHandler extends Handler<CallReceivedMiddlewareContract>
        implements CallReceivedHandlerContract {

    @SafeVarargs
    public CallReceivedHandler(
            ContainerContract container,
            Class<? extends CallReceivedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public CallReceivedResult callReceived(ServiceCallContract call) {
        ServiceResponseContract preCheck = checkCancellation(call, null);
        if (preCheck != null) {
            return new CallReceivedResult(call, preCheck);
        }

        Class<? extends CallReceivedMiddlewareContract> next = this.next;
        if (next == null) {
            return new CallReceivedResult(call, null);
        }

        CallReceivedResult result = getMiddleware(next).callReceived(call, this);

        ServiceResponseContract postCheck = checkCancellation(call, result.response());
        if (postCheck != null) {
            return new CallReceivedResult(result.call(), postCheck);
        }

        return result;
    }
}
