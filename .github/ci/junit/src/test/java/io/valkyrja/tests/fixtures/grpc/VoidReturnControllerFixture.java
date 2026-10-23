/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.routing.attribute.Route;
import io.valkyrja.grpc.routing.attribute.Service;
import io.valkyrja.grpc.routing.data.contract.RouteContract;

/** A route whose own method returns no response. */
@Service(service = "pkg.VoidReturn")
public final class VoidReturnControllerFixture {

    @Route(name = "VoidReturn")
    public void voidReturn(ContainerContract container, RouteContract route) {
        // A handler that returns nothing.
    }
}
