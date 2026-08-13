/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.provider;

import io.valkyrja.application.directory.Directory;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.RequestHandler;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import io.valkyrja.http.server.middleware.CacheResponseMiddleware;
import io.valkyrja.http.server.middleware.routematched.RequestStructMiddleware;
import io.valkyrja.http.server.middleware.routematched.ResponseStructMiddleware;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.log.logger.contract.LoggerContract;
import java.util.Map;
import java.util.function.Consumer;

public class HttpServerServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                RequestHandlerContract.class, HttpServerServiceProvider::publishRequestHandler,
                LogThrowableCaughtMiddleware.class,
                        HttpServerServiceProvider::publishLogThrowableCaughtMiddleware,
                RequestStructMiddleware.class,
                        HttpServerServiceProvider::publishRequestStructMiddleware,
                ResponseStructMiddleware.class,
                        HttpServerServiceProvider::publishResponseStructMiddleware,
                CacheResponseMiddleware.class,
                        HttpServerServiceProvider::publishCacheResponseMiddleware);
    }

    public static void publishRequestHandler(ContainerContract container) {
        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        container.setSingleton(
                RequestHandlerContract.class,
                new RequestHandler(
                        container,
                        container.getSingleton(RouterContract.class),
                        container.getSingleton(RequestReceivedHandlerContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(SendingResponseHandlerContract.class),
                        container.getSingleton(ResponseSentHandlerContract.class),
                        app.getDebugMode()));
    }

    public static void publishLogThrowableCaughtMiddleware(ContainerContract container) {
        container.setSingleton(
                LogThrowableCaughtMiddleware.class,
                new LogThrowableCaughtMiddleware(container.getSingleton(LoggerContract.class)));
    }

    public static void publishRequestStructMiddleware(ContainerContract container) {
        container.setSingleton(RequestStructMiddleware.class, new RequestStructMiddleware());
    }

    public static void publishResponseStructMiddleware(ContainerContract container) {
        container.setSingleton(ResponseStructMiddleware.class, new ResponseStructMiddleware());
    }

    public static void publishCacheResponseMiddleware(ContainerContract container) {
        ApplicationContract app = container.getSingleton(ApplicationContract.class);
        String filePath = Directory.frameworkStorageCachePath("response/");
        container.setSingleton(
                CacheResponseMiddleware.class,
                new CacheResponseMiddleware(filePath, app.getDebugMode()));
    }
}
