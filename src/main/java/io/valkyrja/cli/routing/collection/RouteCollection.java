/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.collection;

import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidRouteNameException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RouteCollection implements RouteCollectionContract {

    protected final Map<String, Supplier<RouteContract>> routes = new LinkedHashMap<>();

    @Override
    public RouteCollection add(RouteContract... commands) {
        for (RouteContract command : commands) {
            routes.put(command.getName(), () -> command);
        }
        return this;
    }

    @Override
    public RouteContract get(String name) {
        Supplier<RouteContract> route = routes.get(name);
        if (route != null) {
            return route.get();
        }
        throw new CliRoutingInvalidRouteNameException("The route `" + name + "` was not found.");
    }

    @Override
    public boolean has(String name) {
        return routes.containsKey(name);
    }

    @Override
    public Map<String, RouteContract> all() {
        return routes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(),
                        (a, b) -> a, LinkedHashMap::new));
    }
}
