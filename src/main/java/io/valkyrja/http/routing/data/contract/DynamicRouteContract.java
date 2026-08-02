/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.data.contract;

import java.util.List;

public interface DynamicRouteContract extends RouteContract {

    String getRegex();

    DynamicRouteContract withRegex(String regex);

    List<ParameterContract> getParameters();

    DynamicRouteContract withParameters(ParameterContract... parameters);

    DynamicRouteContract withAddedParameters(ParameterContract... parameters);

    ParameterContract getParameter(String name);

    boolean hasParameter(String name);
}
