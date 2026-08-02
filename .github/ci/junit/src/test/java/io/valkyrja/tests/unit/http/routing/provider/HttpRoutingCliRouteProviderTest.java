/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.provider.HttpRoutingCliRouteProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingCliRouteProvider}. */
final class HttpRoutingCliRouteProviderTest {

    @Test
    void exposesListCommandController() {
        var provider = new HttpRoutingCliRouteProvider();

        assertEquals(1, provider.getControllerClasses().size());
        assertTrue(provider.getRoutes().isEmpty());
    }

    @Test
    void listHandlerRunsListCommand() {
        var container = mock(ContainerContract.class);
        var command = mock(ListCommand.class);
        var output = mock(OutputContract.class);
        when(container.getSingleton(ListCommand.class)).thenReturn(command);
        when(command.run()).thenReturn(output);

        assertSame(
                output,
                HttpRoutingCliRouteProvider.listHandler(container, mock(RouteContract.class)));
    }
}
