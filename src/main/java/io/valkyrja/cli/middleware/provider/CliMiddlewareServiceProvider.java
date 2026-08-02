/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.middleware.provider;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.cli.middleware.handler.InputReceivedHandler;
import io.valkyrja.cli.middleware.handler.ProcessExitingHandler;
import io.valkyrja.cli.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteMatchedHandler;
import io.valkyrja.cli.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.cli.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CliMiddlewareServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                InputReceivedHandlerContract.class,
                        CliMiddlewareServiceProvider::publishInputReceivedHandler,
                ThrowableCaughtHandlerContract.class,
                        CliMiddlewareServiceProvider::publishThrowableCaughtHandler,
                RouteMatchedHandlerContract.class,
                        CliMiddlewareServiceProvider::publishRouteMatchedHandler,
                RouteNotMatchedHandlerContract.class,
                        CliMiddlewareServiceProvider::publishRouteNotMatchedHandler,
                RouteDispatchedHandlerContract.class,
                        CliMiddlewareServiceProvider::publishRouteDispatchedHandler,
                ProcessExitingHandlerContract.class,
                        CliMiddlewareServiceProvider::publishProcessExitingHandler);
    }

    public static void publishInputReceivedHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                InputReceivedHandlerContract.class,
                new InputReceivedHandler(
                        container, config.inputReceivedMiddleware().toArray(new Class[0])));
    }

    public static void publishThrowableCaughtHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                ThrowableCaughtHandlerContract.class,
                new ThrowableCaughtHandler(
                        container, config.throwableCaughtMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteMatchedHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                RouteMatchedHandlerContract.class,
                new RouteMatchedHandler(
                        container, config.routeMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteNotMatchedHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                RouteNotMatchedHandlerContract.class,
                new RouteNotMatchedHandler(
                        container, config.routeNotMatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishRouteDispatchedHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                RouteDispatchedHandlerContract.class,
                new RouteDispatchedHandler(
                        container, config.routeDispatchedMiddleware().toArray(new Class[0])));
    }

    public static void publishProcessExitingHandler(ContainerContract container) {
        CliConfigContract config = container.getSingleton(CliConfigContract.class);
        container.setSingleton(
                ProcessExitingHandlerContract.class,
                new ProcessExitingHandler(
                        container, config.processExitingMiddleware().toArray(new Class[0])));
    }
}
