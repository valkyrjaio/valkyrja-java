/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data.contract;

import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;

public interface HttpConfigContract extends ConfigContract {
    Integer port();

    List<Class<? extends RequestReceivedMiddlewareContract>> requestReceivedMiddleware();

    List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware();

    List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware();

    List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware();

    List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware();

    List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware();

    List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware();
}
