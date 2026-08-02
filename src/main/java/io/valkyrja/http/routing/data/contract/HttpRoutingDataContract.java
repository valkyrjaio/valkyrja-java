/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.data.contract;

import java.util.Map;
import java.util.function.Supplier;

public interface HttpRoutingDataContract {

    Map<String, Supplier<RouteContract>> routes();

    Map<String, Map<String, String>> paths();

    Map<String, Map<String, String>> dynamicPaths();

    Map<String, Map<String, String>> regexes();
}
