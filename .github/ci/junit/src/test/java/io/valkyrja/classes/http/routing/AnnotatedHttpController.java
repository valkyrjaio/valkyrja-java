/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.http.routing;

import io.valkyrja.classes.http.middleware.PassThroughHttpMiddleware;
import io.valkyrja.classes.http.struct.FailingRequestStructClass;
import io.valkyrja.classes.http.struct.FailingResponseStructClass;
import io.valkyrja.classes.http.struct.ParsedBodyStructClass;
import io.valkyrja.classes.http.struct.ResponseStructClass;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.attribute.DynamicRoute;
import io.valkyrja.http.routing.attribute.Parameter;
import io.valkyrja.http.routing.attribute.Route;
import io.valkyrja.http.routing.attribute.route.Middleware;
import io.valkyrja.http.routing.attribute.route.Name;
import io.valkyrja.http.routing.attribute.route.Path;
import io.valkyrja.http.routing.data.contract.RouteContract;

/** Fully-annotated controller exercising the http attribute route collector. */
@Path("/api")
@Name("api")
public final class AnnotatedHttpController {

    @Route(
            path = "/users",
            name = "index",
            requestMethods = {RequestMethod.POST},
            requestStruct = ParsedBodyStructClass.class,
            responseStruct = ResponseStructClass.class)
    @Path("/v1")
    @Name("list")
    @Middleware(name = PassThroughHttpMiddleware.class)
    @io.valkyrja.http.routing.attribute.route.RequestMethod(requestMethods = {RequestMethod.PUT})
    public ResponseContract index(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(path = "/plain", name = "plain")
    public ResponseContract plain(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(path = "/a", name = "a")
    @Route(path = "/b", name = "b")
    public ResponseContract multi(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/users/{id}/{page}",
            name = "show",
            parameters = {@Parameter(name = "id", regex = "\\d+")})
    @Parameter(name = "page", regex = "\\d+")
    public ResponseContract show(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(path = "/boom", name = "boom")
    public ResponseContract boom(ContainerContract container, RouteContract route) {
        throw new IllegalStateException("handler failure");
    }

    @DynamicRoute(
            path = "/m1/{id}",
            name = "m1",
            parameters = {@Parameter(name = "id", regex = "\\d+")})
    @DynamicRoute(
            path = "/m2/{id}",
            name = "m2",
            parameters = {@Parameter(name = "id", regex = "\\d+")})
    public ResponseContract multiDynamic(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(path = "/mw", name = "mw")
    @Middleware(name = PassThroughHttpMiddleware.class)
    @Middleware(name = PassThroughHttpMiddleware.class)
    public ResponseContract multiMiddleware(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(path = "/p/{a}/{b}", name = "params", parameters = {})
    @Parameter(name = "a", regex = "\\d+")
    @Parameter(name = "b", regex = "\\d+")
    public ResponseContract multiParams(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(
            path = "/badstruct",
            name = "badstruct",
            requestStruct = FailingRequestStructClass.class,
            responseStruct = FailingResponseStructClass.class)
    public ResponseContract badStruct(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }
}