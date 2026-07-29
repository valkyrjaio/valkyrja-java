/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.factory;

import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** Builds the route a declaration describes. */
public final class RouteFactory {

    private RouteFactory() {}

    /**
     * Rebuild a route as the kind its path calls for.
     *
     * <p>A path containing a {@code {parameter}} placeholder describes a dynamic route, so one is
     * returned and the processor can build its regex; any parameters the given route already
     * carries are kept. Every other path describes a static route and is returned as one.
     */
    public static RouteContract fromRoute(RouteContract route) {
        if (route.getPath().contains("{")) {
            List<ParameterContract> parameters =
                    route instanceof DynamicRouteContract dynamic
                            ? dynamic.getParameters()
                            : List.of();

            return new DynamicRoute(
                    route.getPath(),
                    route.getName(),
                    "",
                    parameters,
                    route.getHandler(),
                    route.getRequestMethods(),
                    route.getRouteMatchedMiddleware(),
                    route.getRouteDispatchedMiddleware(),
                    route.getThrowableCaughtMiddleware(),
                    route.getSendingResponseMiddleware(),
                    route.getResponseSentMiddleware(),
                    getRequestStructFromRoute(route),
                    getResponseStructFromRoute(route));
        }

        return new Route(
                route.getPath(),
                route.getName(),
                route.getHandler(),
                route.getRequestMethods(),
                route.getRouteMatchedMiddleware(),
                route.getRouteDispatchedMiddleware(),
                route.getThrowableCaughtMiddleware(),
                route.getSendingResponseMiddleware(),
                route.getResponseSentMiddleware(),
                getRequestStructFromRoute(route),
                getResponseStructFromRoute(route));
    }

    /** Get a route's request struct, or null when it declares none. */
    public static @Nullable RequestStructContract getRequestStructFromRoute(RouteContract route) {
        return route.hasRequestStruct() ? route.getRequestStruct() : null;
    }

    /** Get a route's response struct, or null when it declares none. */
    public static @Nullable ResponseStructContract getResponseStructFromRoute(RouteContract route) {
        return route.hasResponseStruct() ? route.getResponseStruct() : null;
    }
}
