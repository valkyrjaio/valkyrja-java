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

/**
 * A controller with no no-argument constructor, so it is reachable only through the container.
 * Constructing it with {@code new} is what the collector must never do.
 */
@Service(service = "pkg.NoCtor")
public final class NoDefaultConstructorControllerFixture {

    private final String dependency;

    public NoDefaultConstructorControllerFixture(String dependency) {
        this.dependency = dependency;
    }

    @Route(name = "Ping")
    public ServiceResponseContract ping(ContainerContract container, RouteContract route) {
        // The response carries the dependency, so a test reads whether it arrived.
        return ServiceResponse.ok(dependency);
    }
}
