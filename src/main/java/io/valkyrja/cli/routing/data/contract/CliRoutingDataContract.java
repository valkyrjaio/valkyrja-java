/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data.contract;

import java.util.Map;
import java.util.function.Supplier;

public interface CliRoutingDataContract {

    Map<String, Supplier<RouteContract>> routes();
}
