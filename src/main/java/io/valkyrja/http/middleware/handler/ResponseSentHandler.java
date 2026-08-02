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
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;

public class ResponseSentHandler extends Handler<ResponseSentMiddlewareContract>
        implements ResponseSentHandlerContract {

    @SafeVarargs
    public ResponseSentHandler(
            ContainerContract container,
            Class<? extends ResponseSentMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public void responseSent(ServerRequestContract request, ResponseContract response) {
        Class<? extends ResponseSentMiddlewareContract> next = this.next;
        if (next != null) {
            getMiddleware(next).responseSent(request, response, this);
        }
    }
}
