/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
