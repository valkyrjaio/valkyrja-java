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
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;

public class ThrowableCaughtHandler extends Handler<ThrowableCaughtMiddlewareContract>
        implements ThrowableCaughtHandlerContract {

    @SafeVarargs
    public ThrowableCaughtHandler(
            ContainerContract container,
            Class<? extends ThrowableCaughtMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ResponseContract throwableCaught(
            ServerRequestContract request, ResponseContract response, Throwable throwable) {
        Class<? extends ThrowableCaughtMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).throwableCaught(request, response, throwable, this)
                : response;
    }
}
