/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.collection.contract;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.Map;

/** The service map: fully-qualified method name to {@link RouteContract}. */
public interface RouteCollectionContract {

    RouteCollectionContract add(RouteContract... routes);

    RouteContract get(String method);

    boolean has(String method);

    Map<String, RouteContract> all();
}
