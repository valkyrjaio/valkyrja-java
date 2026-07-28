/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.provider.HttpRoutingCliComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingCliRouteProvider;
import io.valkyrja.http.routing.provider.HttpRoutingCliServiceProvider;
import io.valkyrja.http.routing.provider.HttpRoutingComponentProvider;
import io.valkyrja.http.routing.provider.HttpRoutingServiceProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the http routing component, cli-component, and cli-route providers. */
final class HttpRoutingProvidersTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void componentProvider() {
        var provider = new HttpRoutingComponentProvider();

        assertInstanceOf(
                HttpRoutingServiceProvider.class, provider.getContainerProviders(app).get(0));
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void cliComponentProvider() {
        var provider = new HttpRoutingCliComponentProvider();

        assertInstanceOf(
                HttpRoutingCliServiceProvider.class, provider.getContainerProviders(app).get(0));
        assertInstanceOf(HttpRoutingCliRouteProvider.class, provider.getCliProviders(app).get(0));
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void cliRouteProvider() {
        var provider = new HttpRoutingCliRouteProvider();

        assertEquals(List.of(ListCommand.class), provider.getControllerClasses());
        assertTrue(provider.getRoutes().isEmpty());
    }

    @Test
    void cliRouteProviderListHandlerRunsListCommand() {
        var container = new Container();
        var collection = new io.valkyrja.http.routing.collection.RouteCollection();
        var outputFactory =
                mock(
                        io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract
                                .class);
        when(outputFactory.createOutput()).thenReturn(new Output());
        container.setSingleton(ListCommand.class, new ListCommand(collection, outputFactory));

        // listHandler ignores the cli route argument and runs the bound ListCommand.
        OutputContract output =
                HttpRoutingCliRouteProvider.listHandler(
                        container, (io.valkyrja.cli.routing.data.contract.RouteContract) null);

        assertSame(output.getClass(), Output.class);
    }
}
