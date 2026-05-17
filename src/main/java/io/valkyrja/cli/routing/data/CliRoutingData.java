/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.routing.data.contract.CliRoutingDataContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.Map;
import java.util.function.Supplier;

public record CliRoutingData(Map<String, Supplier<RouteContract>> routes)
        implements CliRoutingDataContract {

    public CliRoutingData {
        routes = Map.copyOf(routes);
    }

    public CliRoutingData() {
        this(Map.of());
    }
}
