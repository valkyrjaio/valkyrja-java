/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.routing.provider.CliRoutingCliRouteProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingCliRouteProvider}. */
final class CliRoutingCliRouteProviderTest {

    @Test
    void exposesEmptyControllersAndRoutes() {
        var provider = new CliRoutingCliRouteProvider();

        assertTrue(provider.getControllerClasses().isEmpty());
        assertTrue(provider.getRoutes().isEmpty());
    }
}
