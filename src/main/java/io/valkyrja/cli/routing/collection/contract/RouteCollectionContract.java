/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.collection.contract;

import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.Map;

public interface RouteCollectionContract {

    RouteCollectionContract add(RouteContract... routes);

    RouteContract get(String name);

    boolean has(String name);

    Map<String, RouteContract> all();
}
