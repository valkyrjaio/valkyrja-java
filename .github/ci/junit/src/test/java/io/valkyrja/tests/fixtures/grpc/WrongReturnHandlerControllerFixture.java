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
import io.valkyrja.grpc.routing.attribute.route.RouteHandler;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/**
 * A route naming a handler that returns no response. Its own class, because the collection throws.
 */
@Service(service = "pkg.WrongReturn")
public final class WrongReturnHandlerControllerFixture {

    @Route(name = "WrongReturn")
    @RouteHandler(handlerClass = RouteHandlerFixture.class, handlerMethod = "wrongReturnHandler")
    public ServiceResponseContract wrongReturn(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }
}
