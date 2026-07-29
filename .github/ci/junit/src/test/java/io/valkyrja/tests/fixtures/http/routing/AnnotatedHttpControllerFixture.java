/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.http.routing;

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
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import io.valkyrja.tests.fixtures.http.struct.FailingRequestStructFixture;
import io.valkyrja.tests.fixtures.http.struct.FailingResponseStructFixture;
import io.valkyrja.tests.fixtures.http.struct.ParsedBodyStructFixture;
import io.valkyrja.tests.fixtures.http.struct.ResponseStructFixture;

/** Fully-annotated controller exercising the http attribute route collector. */
@Path("/api")
@Name("api")
public final class AnnotatedHttpControllerFixture {

    @Route(
            path = "/users",
            name = "index",
            requestMethods = {RequestMethod.POST},
            requestStruct = ParsedBodyStructFixture.class,
            responseStruct = ResponseStructFixture.class)
    @Path("/v1")
    @Name("list")
    @Middleware(name = PassThroughHttpMiddlewareFixture.class)
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
    @Middleware(name = PassThroughHttpMiddlewareFixture.class)
    @Middleware(name = PassThroughHttpMiddlewareFixture.class)
    public ResponseContract multiMiddleware(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @DynamicRoute(
            path = "/p/{a}/{b}",
            name = "params",
            parameters = {})
    @Parameter(name = "a", regex = "\\d+")
    @Parameter(name = "b", regex = "\\d+")
    public ResponseContract multiParams(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(
            path = "/badstruct",
            name = "badstruct",
            requestStruct = FailingRequestStructFixture.class,
            responseStruct = FailingResponseStructFixture.class)
    public ResponseContract badStruct(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }

    @Route(path = "/handled", name = "handled")
    @io.valkyrja.http.routing.attribute.route.RouteHandler(
            handlerClass =
                    io.valkyrja.fixtures.http.routing.provider.AnnotatedRouteHandlerProviderClass
                            .class,
            handlerMethod = "handle")
    public ResponseContract handlerAnnotated() {
        // Deliberately parameterless: the @RouteHandler names the handler, so the collector must
        // never try to invoke this method — a controller method may take no arguments at all.
        return new EmptyResponse();
    }

    @Route(path = "/handled-missing", name = "handled.missing")
    @io.valkyrja.http.routing.attribute.route.RouteHandler(
            handlerClass =
                    io.valkyrja.fixtures.http.routing.provider.AnnotatedRouteHandlerProviderClass
                            .class,
            handlerMethod = "doesNotExist")
    public ResponseContract handlerAnnotatedMissing() {
        return new EmptyResponse();
    }
}
