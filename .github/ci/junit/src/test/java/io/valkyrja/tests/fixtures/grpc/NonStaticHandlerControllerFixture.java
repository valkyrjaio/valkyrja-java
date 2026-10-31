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
import io.valkyrja.grpc.routing.attribute.route.Handler;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** A route naming a handler that is not static. */
@Service(service = "pkg.NonStatic")
public final class NonStaticHandlerControllerFixture {

    @Route(name = "NonStatic")
    @Handler(handlerClass = RouteHandlerFixture.class, handlerMethod = "instanceHandler")
    public ServiceResponseContract nonStatic(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("method");
    }
}
