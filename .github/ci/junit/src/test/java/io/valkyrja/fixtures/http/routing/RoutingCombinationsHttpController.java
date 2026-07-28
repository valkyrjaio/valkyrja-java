/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.http.routing;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.attribute.DynamicRoute;
import io.valkyrja.http.routing.attribute.Parameter;
import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.contract.RouteContract;

/**
 * Annotated controller exercising a matrix of dynamic-route parameter types and modifiers, so the
 * annotation construction path can be asserted to produce the same regex as direct construction.
 */
public final class RoutingCombinationsHttpController {

    @DynamicRoute(
            path = "/num/{id}",
            name = "combinations.num",
            parameters = {@Parameter(name = "id", regex = Regex.NUM)})
    public ResponseContract num(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/slug/{slug}",
            name = "combinations.slug",
            parameters = {@Parameter(name = "slug", regex = Regex.SLUG)})
    public ResponseContract slug(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/optional/{opt?}",
            name = "combinations.optional",
            parameters = {@Parameter(name = "opt", regex = Regex.ALPHA, isOptional = true)})
    public ResponseContract optional(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/nc/{nc}",
            name = "combinations.nonCapture",
            parameters = {@Parameter(name = "nc", regex = Regex.ALPHA, shouldCapture = false)})
    public ResponseContract nonCapture(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/multi/{x}/{y}",
            name = "combinations.multi",
            parameters = {
                @Parameter(name = "x", regex = Regex.NUM),
                @Parameter(name = "y", regex = Regex.ALPHA)
            })
    public ResponseContract multi(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }
}
