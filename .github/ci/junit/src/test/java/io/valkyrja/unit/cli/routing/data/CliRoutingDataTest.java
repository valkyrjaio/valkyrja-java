/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.routing.data.CliRoutingData;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingData} record. */
final class CliRoutingDataTest {

    @Test
    void defaultHasNoRoutes() {
        assertTrue(new CliRoutingData().routes().isEmpty());
    }

    @Test
    void routesAccessorAndDefensiveCopy() {
        Map<String, Supplier<RouteContract>> routes = new HashMap<>();
        routes.put("list", () -> new Route("list", "List", (c, r) -> new EmptyOutput()));

        var data = new CliRoutingData(routes);
        routes.clear();

        assertEquals(1, data.routes().size());
        assertTrue(data.routes().containsKey("list"));
    }
}
