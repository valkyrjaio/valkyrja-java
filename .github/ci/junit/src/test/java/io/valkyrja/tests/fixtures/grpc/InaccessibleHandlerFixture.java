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

/** A handler class the collector's package cannot read. */
final class InaccessibleHandlerFixture {

    @SuppressWarnings("unused")
    public static ServiceResponseContract handle(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok();
    }
}
