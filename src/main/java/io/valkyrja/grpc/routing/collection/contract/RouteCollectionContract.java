/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
