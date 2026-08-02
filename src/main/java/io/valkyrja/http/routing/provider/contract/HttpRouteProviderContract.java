/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.provider.contract;

import io.valkyrja.http.routing.data.contract.RouteContract;
import java.util.List;

public interface HttpRouteProviderContract {

    List<Class<?>> getControllerClasses();

    List<RouteContract> getRoutes();
}
