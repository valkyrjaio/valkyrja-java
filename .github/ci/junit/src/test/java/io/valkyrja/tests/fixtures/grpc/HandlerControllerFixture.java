/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.attribute.Route;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.attribute.route.Middleware;
import io.valkyrja.grpc.routing.attribute.route.RouteHandler;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** Fixture controller covering each way a route names its handler, and the route's own members. */
@Service(service = "pkg.Handler")
public final class HandlerControllerFixture {

    /** The handler member names the handler, and the annotated method never runs. */
    @Route(
            name = "Element",
            handler =
                    @RouteHandler(
                            handlerClass = RouteHandlerFixture.class,
                            handlerMethod = "elementHandler"))
    public ServiceResponseContract element(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }

    /** The convenience attribute names the handler. */
    @Route(name = "Annotation")
    @RouteHandler(handlerClass = RouteHandlerFixture.class, handlerMethod = "annotationHandler")
    public ServiceResponseContract annotation(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }

    /** Both spellings name a handler, and the convenience attribute wins. */
    @Route(
            name = "Precedence",
            handler =
                    @RouteHandler(
                            handlerClass = RouteHandlerFixture.class,
                            handlerMethod = "absentHandler"))
    @RouteHandler(handlerClass = RouteHandlerFixture.class, handlerMethod = "annotationHandler")
    public ServiceResponseContract precedence(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }

    /** A convenience attribute naming the absent type names no handler. */
    @Route(name = "VoidHandler")
    @RouteHandler(handlerClass = Void.class, handlerMethod = "")
    public ServiceResponseContract voidHandler(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }

    /** Every remaining member of the route, each declared on the route attribute itself. */
    @Route(
            name = "Typed",
            requestType = String.class,
            responseType = Integer.class,
            routeMatchedMiddleware = GreeterControllerFixture.MatchedMiddleware.class,
            routeDispatchedMiddleware = GreeterControllerFixture.DispatchedMiddleware.class,
            throwableCaughtMiddleware = GreeterControllerFixture.CaughtMiddleware.class,
            sendingResponseMiddleware = GreeterControllerFixture.SendingMiddleware.class,
            responseSentMiddleware = GreeterControllerFixture.ResponseSentMiddleware.class)
    @Middleware(name = GreeterControllerFixture.MatchedMiddleware.class)
    public ServiceResponseContract typed(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }
}
