/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;

/** Middleware run when an earlier stage throws, converting the throwable into a response. */
public interface ThrowableCaughtMiddlewareContract {

    ServiceResponseContract throwableCaught(
            ServiceCallContract call,
            ServiceResponseContract response,
            Throwable throwable,
            ThrowableCaughtHandlerContract handler);
}
