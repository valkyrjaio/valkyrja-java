/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
