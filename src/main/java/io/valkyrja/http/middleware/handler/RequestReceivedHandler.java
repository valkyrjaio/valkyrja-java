/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.handler;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;

public class RequestReceivedHandler extends Handler<RequestReceivedMiddlewareContract>
        implements RequestReceivedHandlerContract {

    @SafeVarargs
    public RequestReceivedHandler(
            ContainerContract container,
            Class<? extends RequestReceivedMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public RequestReceivedResult requestReceived(ServerRequestContract request) {
        Class<? extends RequestReceivedMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).requestReceived(request, this)
                : new RequestReceivedResult(request, null);
    }
}
