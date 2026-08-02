/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.HttpApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import java.util.List;
import java.util.function.Consumer;

public record HttpConfig(
        String namespace,
        String dir,
        String version,
        String environment,
        boolean debugMode,
        String timezone,
        String key,
        String dataPath,
        String dataNamespace,
        Integer port,
        List<ComponentProviderContract> providers,
        List<Consumer<ApplicationContract>> callbacks,
        List<Class<? extends RequestReceivedMiddlewareContract>> requestReceivedMiddleware,
        List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
        List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware,
        List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
        List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
        List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware,
        List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware)
        implements HttpConfigContract {

    public HttpConfig {
        providers = List.copyOf(providers);
        callbacks = List.copyOf(callbacks);
        requestReceivedMiddleware = List.copyOf(requestReceivedMiddleware);
        routeMatchedMiddleware = List.copyOf(routeMatchedMiddleware);
        routeNotMatchedMiddleware = List.copyOf(routeNotMatchedMiddleware);
        routeDispatchedMiddleware = List.copyOf(routeDispatchedMiddleware);
        throwableCaughtMiddleware = List.copyOf(throwableCaughtMiddleware);
        sendingResponseMiddleware = List.copyOf(sendingResponseMiddleware);
        responseSentMiddleware = List.copyOf(responseSentMiddleware);
    }

    public HttpConfig() {
        this(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/http/provider/data",
                "app.http.provider.data",
                8080,
                List.of(new HttpApplicationComponentProvider()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(LogThrowableCaughtMiddleware.class),
                List.of(),
                List.of());
    }
}
