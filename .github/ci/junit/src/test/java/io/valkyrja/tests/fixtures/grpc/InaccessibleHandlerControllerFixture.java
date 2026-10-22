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

/** A route naming a handler on a class the collector's package cannot read. */
@Service(service = "pkg.Inaccessible")
public final class InaccessibleHandlerControllerFixture {

    @Route(name = "Inaccessible")
    @RouteHandler(handlerClass = InaccessibleHandlerFixture.class, handlerMethod = "handle")
    public ServiceResponseContract inaccessible(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }
}
