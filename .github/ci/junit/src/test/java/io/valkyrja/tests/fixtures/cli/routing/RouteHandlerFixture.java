/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.routing;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;

/** A route handler resolved reflectively by {@code @RouteHandler} in attribute routing tests. */
public final class RouteHandlerFixture {

    public OutputContract handle(ContainerContract container, RouteContract route) {
        return new EmptyOutput();
    }
}
