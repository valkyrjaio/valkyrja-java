/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CliRoutingData {

    public final Map<String, Supplier<RouteContract>> routes;

    public CliRoutingData() {
        this(new HashMap<>());
    }

    public CliRoutingData(Map<String, Supplier<RouteContract>> routes) {
        this.routes = Map.copyOf(routes);
    }
}