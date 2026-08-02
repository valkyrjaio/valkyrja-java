/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.routing.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

/**
 * Route-handler provider named by a {@code @RouteHandler} annotation.
 *
 * <p>Shaped like an application's route provider: a static handler taking the container and route,
 * which is how a controller with constructor dependencies is reached — the handler resolves it from
 * the container rather than the collector reflecting a constructor.
 */
public final class AnnotatedRouteHandlerProviderFixture {

    private AnnotatedRouteHandlerProviderFixture() {}

    public static ResponseContract handle(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }
}
