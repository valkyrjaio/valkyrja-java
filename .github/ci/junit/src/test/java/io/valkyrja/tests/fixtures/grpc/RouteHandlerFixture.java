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
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** The static handlers the {@link HandlerControllerFixture} routes name. */
public final class RouteHandlerFixture {

    public static ServiceResponseContract elementHandler(
            ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("element");
    }

    public static ServiceResponseContract annotationHandler(
            ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("annotation");
    }

    /** Takes the handler signature, but an instance method cannot be invoked without one. */
    public ServiceResponseContract instanceHandler(
            ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("instance");
    }

    /** Takes the handler parameters, and returns a response the invocation cannot cast. */
    public static String wrongReturnHandler(ContainerContract container, RouteContract route) {
        return "not a response";
    }
}
