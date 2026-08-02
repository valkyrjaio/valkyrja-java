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
import io.valkyrja.grpc.routing.attribute.Method;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/**
 * Fixture with no accessible no-argument constructor, so the collector's reflective instantiation
 * fails with a {@link ReflectiveOperationException}.
 */
@Service(service = "pkg.NoCtor")
public final class NoDefaultConstructorControllerFixture {

    public NoDefaultConstructorControllerFixture(String required) {
        // Only a parameterized constructor exists.
    }

    @Method(name = "Ping")
    public ServiceResponseContract ping(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok();
    }
}
