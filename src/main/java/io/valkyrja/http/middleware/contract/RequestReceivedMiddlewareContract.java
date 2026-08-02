/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.contract;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;

public interface RequestReceivedMiddlewareContract {

    RequestReceivedResult requestReceived(
            ServerRequestContract request, RequestReceivedHandlerContract handler);
}
