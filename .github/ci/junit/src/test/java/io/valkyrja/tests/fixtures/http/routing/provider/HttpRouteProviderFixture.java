/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.routing.provider;

import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

/** Test HTTP route provider with no controllers or routes. */
public final class HttpRouteProviderFixture implements HttpRouteProviderContract {

    @Override
    public List<Class<?>> getControllerClasses() {
        return List.of();
    }

    @Override
    public List<RouteContract> getRoutes() {
        return List.of();
    }
}
