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
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** Reaches the {@link HiddenControllerFixture} from a test outside its package. */
public final class HiddenControllerAccessFixture {

    public static Class<?> controllerClass() {
        return HiddenControllerFixture.class;
    }

    public static void bind(ContainerContract container) {
        container.setSingleton(HiddenControllerFixture.class, new HiddenControllerFixture());
    }
}

/** A controller the collector's package cannot read, so the invocation fails the access check. */
@Service(service = "pkg.Hidden")
final class HiddenControllerFixture {

    @Route(name = "Hidden")
    public ServiceResponseContract hidden(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok("hidden");
    }
}
