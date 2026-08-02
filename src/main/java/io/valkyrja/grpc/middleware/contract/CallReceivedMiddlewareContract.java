/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;

/** Middleware run once per call before routing. Always runs. */
public interface CallReceivedMiddlewareContract {

    CallReceivedResult callReceived(ServiceCallContract call, CallReceivedHandlerContract handler);
}
