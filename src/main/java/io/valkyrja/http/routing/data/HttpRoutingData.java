/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data;

import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.HttpRoutingDataContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

import java.util.Map;
import java.util.function.Supplier;

public record HttpRoutingData(
        Map<String, Supplier<RouteContract>> routes,
        Map<String, Map<String, String>> paths,
        Map<String, Map<String, String>> dynamicPaths,
        Map<String, Map<String, String>> regexes
) implements HttpRoutingDataContract {
    public HttpRoutingData() {
        this(Map.of(), Map.of(), Map.of(), Map.of());
    }
}
